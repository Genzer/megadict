package com.megadict.activity;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.application.MegaDictApp;
import com.megadict.business.DictionaryClient;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.SearchTask;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;

public class DictionaryActivity extends BaseActivity implements OnClickListener, OnEditorActionListener {
	// Activity control variables.
	private EditText searchEditText;
	private WebView resultView;
	private ProgressBar progressBar;

	// Member variables
	private DictionaryClient dictionaryClient;
	private SQLiteDatabase database;
	private ResultTextMaker resultTextMaker;
	private SearchTask searchTask;


	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();
		initVariables();
		setStartPage();
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
	public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH
				|| actionId == EditorInfo.IME_ACTION_DONE
				|| event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		}
		return true;
	}

	// ========================= Private functions ======================= //
	private void initLayout() {
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		searchButton.setOnEditorActionListener(this);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		resultView = (WebView)findViewById(R.id.resultView);
		resultView.setBackgroundColor(0x00000000);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
	}

	private void doSearching(final String word) {
		if(searchTask == null || !searchTask.isSearching()) {
			searchTask = new SearchTask(dictionaryClient, resultTextMaker,
					progressBar, resultView, getString(R.string.noDictionary));
			searchTask.execute(word);

		} else {
			Utility.messageBox(this, "Seaching... Please wait.");
		}
	}

	private void initVariables() {
		// Get application-scoped variables.
		dictionaryClient = ((MegaDictApp) getApplication()).dictionaryClient;
		// Create and open database.
		final DatabaseHelper helper = new DatabaseHelper(this);
		database = helper.getReadableDatabase();
		// Scan chosen databases when MegaDict opens.
		dictionaryClient.scanDatabase(this, database);
		// Init result text maker.
		resultTextMaker = new ResultTextMaker(getAssets());
		// Init progress dialog.
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Searching...");
	}

	private void setStartPage() {
		final String welcomeStr = resultTextMaker.getWelcomeHTML(dictionaryClient.getDictionaryNames());
		resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeStr, "text/html", "utf-8", null);
	}
}
