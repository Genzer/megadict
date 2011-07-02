package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.application.MegaDictApp;
import com.megadict.business.DictionaryClient;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;

public class DictionaryActivity extends Activity implements OnClickListener, android.view.View.OnKeyListener, OnEditorActionListener {
	private static final String TAG = "DictionaryActivity";
	private DictionaryClient dictionaryClient;
	private EditText searchEditText;
	private TextView resultTextView;
	private SQLiteDatabase database;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		initLayout();

		// Get application-scoped variables.
		dictionaryClient = ((MegaDictApp) getApplication()).dictionaryClient;

		// Create and open database.
		final DatabaseHelper helper = new DatabaseHelper(this);
		database = helper.getReadableDatabase();

		// Scan chosen databases.
		dictionaryClient.scanChosenDictionaries(getChosenDictionaries());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.settingMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.SettingActivity");
		} else if (item.getItemId() == R.id.manageMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.ManageActivity");
		} else if(item.getItemId() == R.id.aboutMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.AboutActivity");
		}
		return true;
	}

	@Override
	public void onClick(final View view) {
		if (view.getId() == R.id.searchButton) {
			searchWord(searchEditText.getText().toString());
		}
	}

	@Override
	public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
		return false;
	}

	@Override
	public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH
				|| actionId == EditorInfo.IME_ACTION_DONE
				|| event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		}
		return true;
	}

	// // ========================= Private functions ======================= ////
	private void initLayout() {
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		searchButton.setOnEditorActionListener(this);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		resultTextView = (TextView) findViewById(R.id.resultTextView);
		resultTextView.setText("");
	}

	private List<Pair<String, String>> getChosenDictionaries() {
		final Cursor cursor = ChosenModel.selectChosenDictionaries(database);
		startManagingCursor(cursor);

		final List<Pair<String, String>> list =
			new ArrayList<Pair<String, String>>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final Pair<String, String> pair =
				new Pair<String, String>(cursor.getString(cursor.getColumnIndex(ChosenModel.INDEX_PATH_COLUMN)), cursor.getString(cursor.getColumnIndex(ChosenModel.DICT_PATH_COLUMN)));
			list.add(pair);
		}
		return list;
	}

	private void searchWord(final String word) {
		final String result = dictionaryClient.lookup(word);
		resultTextView.setText(result);
	}
}
