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
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.TextSelector;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.business.searching.WordSearcher;
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
	public DictionaryComponent dictionaryComponent;
	private SQLiteDatabase database;
	private TextSelector textSelector;
	private WordSearcher searcher;
	private DictionaryScanner scanner;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSomething();
		// Scan chosen databases when MegaDict opens.
		doScanningStorage();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(Utility.isLocaleChanged()) {
			// Change noDefinition string.
			searcher.setNoDefinitionStr(getString(R.string.noDefinition));
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		scanner.updateDictionaryModels(this, dictionaryComponent);
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
	private void initSomething() {
		// Create and open database.
		final DatabaseHelper helper = new DatabaseHelper(this);
		database = helper.getReadableDatabase();

		// Create UI components.
		final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		final AutoCompleteTextView searchBar = (AutoCompleteTextView)findViewById(R.id.searchEditText);
		final ResultTextMaker resultTextMaker = new ResultTextMaker(getAssets());
		resultView = new ResultView(this);

		// Create dictionaryComponent which contain UI components.
		dictionaryComponent = new DictionaryComponent.Builder().
				searchButton(searchButton).
				searchBar(searchBar).resultView(resultView).
				resultTextMaker(resultTextMaker).
				database(database).
				progressBar(progressBar).
				context(this).build();

		// Get scanner from application.
		scanner = ((MegaDictApp) getApplication()).scanner;

		// Create searcher, recommender and businessComponent.
		final WordRecommender recommender = new WordRecommender(scanner.getDictionaryModels(), dictionaryComponent);
		searcher = new WordSearcher(scanner.getDictionaryModels(), dictionaryComponent);
		final BusinessComponent businessComponent = new BusinessComponent(searcher, recommender);
		// Register observers for scanner.
		scanner.addObserver(searcher);
		scanner.addObserver(recommender);

		// Init search button.
		final SearchButtonInitializer searchButtonInitializer = new SearchButtonInitializer(this, businessComponent, dictionaryComponent);
		searchButtonInitializer.doNothing();

		// Init search bar.
		final SearchBarInitializer searchBarInitializer = new SearchBarInitializer(this, businessComponent, dictionaryComponent);
		// Register observers for sbInitializer.
		searchBarInitializer.addObserver(recommender);

		// Init Result view.
		final ResultViewInitializer resultViewInitializer = new ResultViewInitializer(this, businessComponent, dictionaryComponent);
		resultViewInitializer.doNothing();

		// Init result panel.
		final LinearLayout resultPanel = (LinearLayout)findViewById(R.id.resultPanel);
		resultPanel.addView(resultView);

		// Init text selector.
		textSelector = new TextSelector();
	}

	private void doScanningStorage() {
		if(!scanner.scanStorage(this, dictionaryComponent)) {
			Utility.messageBox(this, getString(R.string.scanning));
		}
	}
}
