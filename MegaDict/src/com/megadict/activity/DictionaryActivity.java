package com.megadict.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import com.megadict.preferences.LanguagePreference;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public final class DictionaryActivity extends BaseActivity {
	/*
	 *  I am not satisfied with using this non-final static variable
	 *  because it is a bad practice, but I can't find any better solution.
	 */
	public static boolean activityRunning;

	// Activity control variables.
	private ResultView resultView;

	// Member variables
	private DictionaryComponent dictionaryComponent;
	private WordSearcher searcher;
	private DictionaryScanner scanner;
	private TextSelector textSelector;
	private HistoryDisplayer historyDisplayer;
	private PronounceButtonInitializer pronounceButtonInitializer;
	private final Map<Button, Integer> bottomButtonMap = new HashMap<Button, Integer>();
	private LanguagePreference languagePreference;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Init preferences.
		languagePreference = LanguagePreference.newInstance(this);
		// Load language from preference.
		languagePreference.loadLanguageFromPreference();

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

		if(languagePreference.isLanguageChanged()) {
			//========= Reset texts ======//
			// Change noDefinition string.
			searcher.setNoDefinitionStr(getString(R.string.noDefinition));
			// Redraw search bar hint
			dictionaryComponent.getSearchBar().setHint(R.string.searchBarHint);
			// Redraw bottom buttons.
			final Set<Button> buttons = bottomButtonMap.keySet();
			for(final Button button : buttons) {
				button.setText(bottomButtonMap.get(button));
			}
		}

		// Update dictionary models.
		scanner.updateDictionaryModels(dictionaryComponent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		languagePreference.resetLanguageChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pronounceButtonInitializer.shutDownMegaSpeaker();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		inflateMenu(menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		// Redraw main menu.
		if(languagePreference.isLanguageChanged()) {
			menu.clear();
			inflateMenu(menu);
		}
		return true;
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
			textSelector.selectText(this, resultView);
		} else if (item.getItemId() == R.id.historyMenuItem) {
			historyDisplayer.showHistoryDialog(searcher.getHistoryList());
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
		final Button settingButton = (Button)findViewById(R.id.selectButton);
		final Button moreButton = (Button)findViewById(R.id.moreButton);
		bottomButtonMap.put(manageButton, R.string.manageButtonLabel);
		bottomButtonMap.put(historyButton, R.string.historyButtonLabel);
		bottomButtonMap.put(settingButton, R.string.selectButtonLabel);
		bottomButtonMap.put(moreButton, R.string.moreButtonLabel);

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
				bottomButtons(new ArrayList<Button>(bottomButtonMap.keySet())).build();

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
		searchButtonInitializer.init();

		// Init pronounce button.
		pronounceButtonInitializer = new PronounceButtonInitializer(dictionaryComponent);

		// Init search bar.
		final SearchBarInitializer searchBarInitializer =
				new SearchBarInitializer(businessComponent, dictionaryComponent);
		searchBarInitializer.init();
		searchBarInitializer.addObserver(recommender);

		// Init Result view.
		final ResultViewInitializer resultViewInitializer =
				new ResultViewInitializer(businessComponent, dictionaryComponent);
		resultViewInitializer.init();
		resultViewInitializer.addObserver(recommender);

		// Init history retriever.
		historyDisplayer = new HistoryDisplayer(businessComponent, dictionaryComponent);
		historyDisplayer.init();
		historyDisplayer.addObserver(recommender);

		// Init result panel.
		final LinearLayout resultPanel =
				(LinearLayout) findViewById(R.id.resultPanel);
		resultPanel.addView(resultView);

		// Init text selector.
		textSelector = new TextSelector();

		// Init bottom buttons.
		final BottomButtonsInitializer bottomInitializer = new BottomButtonsInitializer(historyDisplayer, textSelector, businessComponent, dictionaryComponent);
		bottomInitializer.init();
	}

	private void doScanningStorage() {
		scanner.scanStorage(dictionaryComponent);
	}

	private void inflateMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
	}
}
