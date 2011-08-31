package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.megadict.business.HistoryDisplayer;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.TextSelector;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.business.searching.WordSearcher;
import com.megadict.initializer.BottomButtonsInitializer;
import com.megadict.initializer.PronounceButtonInitializer;
import com.megadict.initializer.ResultViewInitializer;
import com.megadict.initializer.SearchBarInitializer;
import com.megadict.initializer.SearchButtonInitializer;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public final class DictionaryActivity extends BaseActivity {
	public static boolean activityRunning;
	private static final String TAG = "DictionaryActivity";

	// Activity control variables.
	private ResultView resultView;

	// Member variables
	private DictionaryComponent dictionaryComponent;
	private WordSearcher searcher;
	private DictionaryScanner scanner;
	private TextSelector textSelector;
	private HistoryDisplayer historyDisplayer;
	private PronounceButtonInitializer pronounceButtonInitializer;
	final List<Button> bottomButtons = new ArrayList<Button>();;

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
	protected void onResume() {
		super.onResume();
		activityRunning = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		activityRunning = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (Utility.isLocaleChanged()) {
			// Change noDefinition string.
			searcher.setNoDefinitionStr(getString(R.string.noDefinition));
			dictionaryComponent.getSearchBar().setHint(R.string.searchBarHint);
			for(final Button button : bottomButtons) {
				button.invalidate();
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		scanner.updateDictionaryModels(dictionaryComponent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pronounceButtonInitializer.shutDownMegaSpeaker();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if (Utility.isLocaleChanged()) {
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
		} else if (item.getItemId() == R.id.aboutMenuItem) {
			Utility.startActivity(this, "com.megadict.activity.AboutActivity");
		} else if (item.getItemId() == R.id.selectTextMenuItem) {
			textSelector.selectText(this, resultView, TAG);
		} else if (item.getItemId() == R.id.historyMenuItem) {
			final List<String> list = searcher.getHistoryList();
			if (list.isEmpty()) {
				Utility.messageBox(this, R.string.emptyHistory);
			} else {
				historyDisplayer.showHistoryDialog(list);
			}
		}
		return true;
	}

	// ========================= Private functions ======================= //
	private void initSomething() {
		// Create UI components.
		final ProgressBar progressBar =
				(ProgressBar) findViewById(R.id.progressBar);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		final Button pronounceButton = (Button)findViewById(R.id.pronounceButton);
		final AutoCompleteTextView searchBar = (AutoCompleteTextView) findViewById(R.id.searchEditText);
		// Bottom buttons.
		final Button manageButton = (Button)findViewById(R.id.manageButton);
		final Button historyButton = (Button)findViewById(R.id.historyButton);
		final Button settingButton = (Button)findViewById(R.id.settingButton);
		final Button moreButton = (Button)findViewById(R.id.moreButton);
		bottomButtons.add(manageButton);
		bottomButtons.add(historyButton);
		bottomButtons.add(settingButton);
		bottomButtons.add(moreButton);
		// Result text maker.
		final ResultTextMaker resultTextMaker =	new ResultTextMaker(getAssets());
		resultView = new ResultView(this);

		// Create dictionaryComponent which contain UI components.
		dictionaryComponent =
				new DictionaryComponent.Builder().
				searchButton(searchButton).
				pronounceButton(pronounceButton).
				searchBar(searchBar).
				resultView(resultView).
				resultTextMaker(resultTextMaker).
				progressBar(progressBar).
				context(this).
				bottomButtons(bottomButtons).build();

		// Get scanner from application.
		scanner = ((MegaDictApp) getApplication()).scanner;

		// Create searcher, recommender and businessComponent.
		final WordRecommender recommender =
				new WordRecommender(scanner.getDictionaryModels(), dictionaryComponent);
		searcher =
				new WordSearcher(scanner.getDictionaryModels(), dictionaryComponent);
		final BusinessComponent businessComponent =
				new BusinessComponent(searcher, recommender);
		// Register observers for scanner.
		scanner.addObserver(searcher);
		scanner.addObserver(recommender);

		// Init search button.
		final SearchButtonInitializer searchButtonInitializer =
				new SearchButtonInitializer(businessComponent, dictionaryComponent);
		searchButtonInitializer.doNothing();

		// Init pronounce button.
		pronounceButtonInitializer = new PronounceButtonInitializer(dictionaryComponent);

		// Init search bar.
		final SearchBarInitializer searchBarInitializer =
				new SearchBarInitializer(businessComponent, dictionaryComponent);
		searchBarInitializer.addObserver(recommender);

		// Init Result view.
		final ResultViewInitializer resultViewInitializer =
				new ResultViewInitializer(businessComponent, dictionaryComponent);
		resultViewInitializer.addObserver(recommender);

		// Init history retriever.
		historyDisplayer = new HistoryDisplayer(businessComponent, dictionaryComponent);
		historyDisplayer.addObserver(recommender);

		// Init result panel.
		final LinearLayout resultPanel =
				(LinearLayout) findViewById(R.id.resultPanel);
		resultPanel.addView(resultView);

		// Init text selector.
		textSelector = new TextSelector();

		// Init bottom buttons.
		final BottomButtonsInitializer bottomInitializer = new BottomButtonsInitializer(businessComponent, dictionaryComponent);
	}

	private void doScanningStorage() {
		scanner.scanStorage(dictionaryComponent);
	}
}
