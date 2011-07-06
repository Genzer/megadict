package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.application.MegaDictApp;
import com.megadict.business.DictionaryClient;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;

public class DictionaryActivity extends BaseActivity implements OnClickListener, android.view.View.OnKeyListener, OnEditorActionListener {
	private static final String TAG = "DictionaryActivity";
	private DictionaryClient dictionaryClient;
	private EditText searchEditText;
	private TextView resultTextView;
	private SQLiteDatabase database;
	private ProgressDialog progressDialog;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.search);
		initLayout();

		// Init progress dialog
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Searching...");


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
			doSearching(searchEditText.getText().toString());
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

	private void doSearching(final String word) {
		final SearchTask task = new SearchTask();
		task.execute(word);
	}

	private class SearchTask extends AsyncTask<String, Void, String> {
		private ProgressBar progressBar;
		@Override
		protected void onPreExecute() {
			progressBar = (ProgressBar)findViewById(R.id.progressBar);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(final String... params) {
			return search(params[0]);
		}

		@Override
		protected void onPostExecute(final String content) {
			updateResult(content);
			progressBar.setVisibility(View.INVISIBLE);
		}

		private String search(final String word) {
			return dictionaryClient.lookup(word);
		}

		private void updateResult(final String content) {
			resultTextView.setText(content);
		}
	}

}

