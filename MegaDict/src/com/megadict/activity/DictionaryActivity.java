package com.megadict.activity;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.activity.base.BaseActivity;
import com.megadict.application.MegaDictApp;
import com.megadict.bean.RecommendComponent;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.bean.SearchComponent;
import com.megadict.business.DictionaryClient;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.TextSelector;
import com.megadict.initializer.ResultViewInitializer;
import com.megadict.initializer.SearchBarInitializer;
import com.megadict.initializer.SearchButtonInitializer;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public final class DictionaryActivity extends BaseActivity {
	private static final String TAG = "DictionaryActivity";

	// Activity control variables.
	public ResultView resultView;

	// Member variables
	public DictionaryClient dictionaryClient;
	public ScanStorageComponent scanStorageComponent;
	public SQLiteDatabase database;
	public TextSelector textSelector;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initVariables();
		initLayout();
		// Scan chosen databases when MegaDict opens.
		doScanningStorage();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(Utility.isLocaleChanged()) {
			// Change noDefinition string.
			dictionaryClient.setNoDefinitionString(getString(R.string.noDefinition));
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		dictionaryClient.updateDictionaryModels(this, database, scanStorageComponent);
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
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if(Utility.isLocaleChanged()) {
			menu.clear();
			final MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_menu, menu);
			Utility.setLocaleChanged(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.settingMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.SettingActivity");
		} else if (item.getItemId() == R.id.manageMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.ManageActivity");
		} else if(item.getItemId() == R.id.aboutMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.AboutActivity");
		} else if(item.getItemId() == R.id.selectTextMenuItem) {
			textSelector.selectText(this, resultView, TAG);
		}
		return true;
	}

	// ========================= Private functions ======================= //
	private void initLayout() {
		final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		final AutoCompleteTextView searchBar = (AutoCompleteTextView)findViewById(R.id.searchEditText);
		final ResultTextMaker resultTextMaker = new ResultTextMaker(getAssets());
		resultView = new ResultView(this);

		// Init useful components.
		final SearchComponent searchComponent = new SearchComponent(resultView, resultTextMaker, progressBar);
		final RecommendComponent recommendComponent = new RecommendComponent(searchBar, resultView, progressBar);
		scanStorageComponent = new ScanStorageComponent(resultView, resultTextMaker, this);

		// Init search button.
		SearchButtonInitializer.init(this, dictionaryClient, searchButton, searchBar, searchComponent);

		// Init search bar.
		SearchBarInitializer.init(this, dictionaryClient, searchBar, searchComponent, recommendComponent);

		// Init Result view.
		ResultViewInitializer.init(this, dictionaryClient, searchBar, searchComponent);

		// Init result panel.
		final LinearLayout resultPanel = (LinearLayout)findViewById(R.id.resultPanel);
		resultPanel.addView(resultView);
	}

	private void initVariables() {
		// Get application-scoped variables.
		dictionaryClient = ((MegaDictApp) getApplication()).dictionaryClient;
		// Create and open database.
		final DatabaseHelper helper = new DatabaseHelper(this);
		database = helper.getReadableDatabase();
		// Init text selector.
		textSelector = new TextSelector();
	}

	private void doScanningStorage() {
		if(!dictionaryClient.scanStorage(this, database, scanStorageComponent)) {
			Utility.messageBox(this, getString(R.string.scanning));
		}
	}
}
