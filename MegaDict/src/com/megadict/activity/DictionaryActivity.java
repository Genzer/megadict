package com.megadict.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.activity.base.AbstractActivity;
import com.megadict.application.MegaDictApp;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.HistoryDisplayer;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.business.searching.WordSearcher;
import com.megadict.initializer.BottomButtonsInitializer;
import com.megadict.initializer.PronounceButtonInitializer;
import com.megadict.initializer.ResultViewInitializer;
import com.megadict.initializer.SearchBarInitializer;
import com.megadict.initializer.SearchButtonInitializer;
import com.megadict.preferences.LanguagePreference;
import com.megadict.preferences.SpeakerPreference;
import com.megadict.utility.ActivityHelper;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public final class DictionaryActivity extends AbstractActivity {
	// Activity control variables.
	private Button searchButton, pronounceButton;
	private ResultView resultView;
	private AutoCompleteTextView searchBar;
	private ProgressBar progressBar;
	private ResultTextMaker resultTextMaker;


	// Member variables
	private DictionaryComponent dictionaryComponent;
	private BusinessComponent businessComponent;
	private DictionaryScanner scanner;
	private HistoryDisplayer historyDisplayer;
	private PronounceButtonInitializer pronounceButtonInitializer;
	private final Map<Button, Integer> bottomButtonMap = new HashMap<Button, Integer>();
	private boolean languageChanged;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Retain business component.
		return businessComponent;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUIs();
		initBottomButtons();
		initResultTextMakerAndResultView();
		initDictionaryComponent();
		initBusinessComponent();
		initInitializers();

		// Load language when first start app.
		Utility.updateLocale(this, LanguagePreference.newInstance(this).getLanguage());
		refreshStrings();
		// Set this to update menus.
		languageChanged = true;

		// Scan chosen databases when MegaDict opens.
		doScanningStorage();

		// Check Internet connection.
		if(!Utility.isOnline(this)) {
			Utility.messageBox2(this, R.string.internetNotConnectedWarning);
		}
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Update dictionary models.
		scanner.updateDictionaryModels(dictionaryComponent);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Reset languageChanged.
		languageChanged = false;
		// Cancel recommending to prevent it throw RuntimeException on showDropdown().
		businessComponent.getRecommender().cancelRecommending();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		pronounceButtonInitializer.shutDownTTSSpeaker();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		inflateMenu(menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// Redraw main menu.
		if(languageChanged) {
			menu.clear();
			inflateMenu(menu);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.settingMenuItem) {
			Utility.startActivityForResult(this, ActivityHelper.SETTING_ACTIVITY, ActivityHelper.SETTING_REQUEST);
		} else if (item.getItemId() == R.id.manageMenuItem) {
			Utility.startActivity(this, ActivityHelper.MANAGE_ACTIVITY);
		} else if (item.getItemId() == R.id.aboutMenuItem) {
			Utility.startActivity(this, ActivityHelper.ABOUT_ACTIVITY);
		} else if (item.getItemId() == R.id.selectTextMenuItem) {
			Utility.selectText(this, resultView);
		} else if (item.getItemId() == R.id.historyMenuItem) {
			historyDisplayer.showHistoryDialog(businessComponent.getSearcher().getHistoryList());
		}
		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(requestCode == ActivityHelper.SETTING_REQUEST && resultCode == Activity.RESULT_OK) {
			if(data.getBooleanExtra(LanguagePreference.LANGUAGE_CHANGED, false)) {
				refreshStrings();
				languageChanged = true;
			}

			if(data.getBooleanExtra(SpeakerPreference.SPEAKER_TYPE_CHANGED, false)) {
				pronounceButtonInitializer.updateSpeakerType();
			}

			if(data.getBooleanExtra(SpeakerPreference.SPEAKER_LANGUAGE_CHANGED, false)) {
				pronounceButtonInitializer.updateSpeakerLanguage();
			}
		}
	}

	// ========================= Private functions ======================= //
	private void doScanningStorage() {
		scanner.scanStorage(dictionaryComponent);
	}

	private void inflateMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
	}

	private void refreshStrings() {
		// Change noDefinition string.
		businessComponent.getSearcher().setNoDefinitionStr(getString(R.string.noDefinition));
		// Redraw search bar hint
		dictionaryComponent.getSearchBar().setHint(R.string.searchBarHint);
		// Redraw bottom buttons.
		final Set<Button> buttons = bottomButtonMap.keySet();
		for(final Button button : buttons) {
			button.setText(bottomButtonMap.get(button));
		}
	}




	// ==================== Init functions =================//
	private void initBottomButtons() {
		final Button manageButton = (Button)findViewById(R.id.manageButton);
		final Button historyButton = (Button)findViewById(R.id.historyButton);
		final Button settingButton = (Button)findViewById(R.id.selectButton);
		final Button moreButton = (Button)findViewById(R.id.moreButton);
		bottomButtonMap.put(manageButton, R.string.manageButtonLabel);
		bottomButtonMap.put(historyButton, R.string.historyButtonLabel);
		bottomButtonMap.put(settingButton, R.string.selectButtonLabel);
		bottomButtonMap.put(moreButton, R.string.moreButtonLabel);
	}

	private void initUIs() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		searchButton = (Button) findViewById(R.id.searchButton);
		pronounceButton = (Button)findViewById(R.id.pronounceButton);
		searchBar = (AutoCompleteTextView) findViewById(R.id.searchEditText);
	}

	private void initResultTextMakerAndResultView() {
		resultTextMaker = new ResultTextMaker(getAssets());
		resultView = new ResultView(this);
	}

	private void initDictionaryComponent() {
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
	}

	private void initBusinessComponent() {
		// Get scanner from application.
		scanner = ((MegaDictApp) getApplication()).scanner;

		// Get saved instance.
		final BusinessComponent bc = (BusinessComponent)getLastNonConfigurationInstance();
		WordSearcher searcher;
		WordRecommender recommender;
		if(bc == null) {
			// Init searcher and recommender.
			recommender = new WordRecommender(scanner.getDictionaryModels(), dictionaryComponent);
			searcher = new WordSearcher(scanner.getDictionaryModels(), dictionaryComponent);
			// Init business component.
			businessComponent =	new BusinessComponent(searcher, recommender);
		} else {
			businessComponent = bc;
			recommender = businessComponent.getRecommender();
			searcher = businessComponent.getSearcher();
		}

		// Register observers for scanner.
		scanner.addObserver(searcher);
		scanner.addObserver(recommender);
	}

	private void initInitializers() {
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
		searchBarInitializer.addObserver(businessComponent.getRecommender());

		// Init Result view.
		final ResultViewInitializer resultViewInitializer =
				new ResultViewInitializer(businessComponent, dictionaryComponent);
		resultViewInitializer.init();
		resultViewInitializer.addObserver(businessComponent.getRecommender());

		// Init history retriever.
		historyDisplayer = new HistoryDisplayer(businessComponent, dictionaryComponent);
		historyDisplayer.init();
		historyDisplayer.addObserver(businessComponent.getRecommender());

		// Init result panel.
		final LinearLayout resultPanel =
				(LinearLayout) findViewById(R.id.resultPanel);
		resultPanel.addView(resultView);

		// Init bottom buttons.
		final BottomButtonsInitializer bottomInitializer = new BottomButtonsInitializer(historyDisplayer, businessComponent, dictionaryComponent);
		bottomInitializer.init();
	}

}
