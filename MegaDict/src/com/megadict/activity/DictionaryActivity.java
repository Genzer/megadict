package com.megadict.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
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
	// Application-scope variables.
	private DictionaryScanner scanner;
	private ResultTextMaker resultTextMaker;

	// Member variables
	private DictionaryComponent dictionaryComponent;
	private BusinessComponent businessComponent;
	private HistoryDisplayer historyDisplayer;
	private PronounceButtonInitializer pronounceButtonInitializer;
	private final Map<Button, Integer> bottomButtonMap = new HashMap<Button, Integer>();
	private boolean languageChanged;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		scanner.deleteObservers();
		// Remove the dictionary component from the recommender.
		businessComponent.getRecommender().setDictionaryComponent(null);
		// Remove the dictionary component from the searcher.
		businessComponent.getSearcher().setDictionaryComponent(null);

		// Retain business component.
		return businessComponent;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initSomething();

		// Load language when first start app.
		Utility.updateLocale(this, LanguagePreference.newInstance(this).getLanguage());
		refreshStrings();
		// Set this to update menus.
		languageChanged = true;

		// Scan chosen databases when MegaDict opens.
		scanner.scanStorage(dictionaryComponent);

		// Check Internet connection.
		if(!Utility.isOnline(this)) {
			Utility.messageBoxLong(this, R.string.internetNotConnectedWarning);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Reset languageChanged.
		languageChanged = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		businessComponent.getRecommender().setDictionaryComponent(null);
		businessComponent.getSearcher().setDictionaryComponent(null);
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
			Utility.startActivityForResult(this, ActivityHelper.MANAGE_ACTIVITY, ActivityHelper.MANAGE_REQUEST);
		} else if (item.getItemId() == R.id.aboutMenuItem) {
			Utility.startActivity(this, ActivityHelper.ABOUT_ACTIVITY);
		} else if (item.getItemId() == R.id.selectTextMenuItem) {
			Utility.selectText(this, dictionaryComponent.getResultView());
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

		if(requestCode == ActivityHelper.MANAGE_REQUEST && resultCode == Activity.RESULT_OK &&
				data.getBooleanExtra(DictionaryScanner.MODEL_CHANGED, false)) {
			scanner.updateDictionaryModels(dictionaryComponent);
		}
	}


	// ========================= Private functions ======================= //
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
		// Refresh start page.
		refreshStartPage();
	}

	private void refreshStartPage() {
		final int dictCount = scanner.getDictionaryModels().size();
		final String welcomeStr =
				(dictCount > 1
						? dictionaryComponent.getContext().getString(R.string.usingDictionaryPlural, dictCount)
								: dictionaryComponent.getContext().getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = resultTextMaker.getWelcomeHTML(welcomeStr);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}


	// ==================== Init functions =================//
	private void initSomething() {
		initDictionaryComponent();
		initBusinessComponent();
		initInitializers();
		// Init result panel.
		//((LinearLayout)findViewById(R.id.resultPanel)).addView(resultView);
	}

	private void initDictionaryComponent() {
		// Init UIs.
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		final Button pronounceButton = (Button)findViewById(R.id.pronounceButton);
		final AutoCompleteTextView searchBar = (AutoCompleteTextView) findViewById(R.id.searchEditText);
		final ResultView resultView = new ResultView(this);
		((LinearLayout)findViewById(R.id.resultPanel)).addView(resultView);
		final Button manageButton = (Button)findViewById(R.id.manageButton);
		final Button historyButton = (Button)findViewById(R.id.historyButton);
		final Button settingButton = (Button)findViewById(R.id.selectButton);
		final Button moreButton = (Button)findViewById(R.id.moreButton);

		// Store bottom buttons.
		bottomButtonMap.put(manageButton, R.string.manageButtonLabel);
		bottomButtonMap.put(historyButton, R.string.historyButtonLabel);
		bottomButtonMap.put(settingButton, R.string.selectButtonLabel);
		bottomButtonMap.put(moreButton, R.string.moreButtonLabel);

		// Init dictionary component.
		dictionaryComponent =
				new DictionaryComponent.Builder().
				searchButton(searchButton).
				pronounceButton(pronounceButton).
				searchBar(searchBar).
				resultView(resultView).
				progressBar(progressBar).
				context(this).
				bottomButtons(new ArrayList<Button>(bottomButtonMap.keySet())).build();
	}

	private void initBusinessComponent() {
		// Get application variables.
		scanner = ((MegaDictApp) getApplication()).scanner;
		resultTextMaker = ((MegaDictApp) getApplication()).resultTextMaker;

		// Get saved instance.
		final BusinessComponent bc = (BusinessComponent)getLastNonConfigurationInstance();
		WordSearcher searcher;
		WordRecommender recommender;
		if(bc == null) {
			// Init searcher and recommender.
			recommender = new WordRecommender(scanner.getDictionaryModels(), dictionaryComponent);
			searcher = new WordSearcher(scanner.getDictionaryModels(), resultTextMaker, dictionaryComponent);
			// Init business component.
			businessComponent =	new BusinessComponent(searcher, recommender);
		} else {
			businessComponent = bc;

			// Restore recommender.
			recommender = businessComponent.getRecommender();
			// Reset new dictionary component in recommender.
			recommender.setDictionaryComponent(dictionaryComponent);

			// Restore searcher.
			searcher = businessComponent.getSearcher();
			// Reset new dictionary component in searcher.
			searcher.setDictionaryComponent(dictionaryComponent);
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

		// Init history displayer.
		historyDisplayer = new HistoryDisplayer(businessComponent, dictionaryComponent);
		historyDisplayer.init();
		historyDisplayer.addObserver(businessComponent.getRecommender());

		// Init bottom buttons.
		final BottomButtonsInitializer bottomInitializer = new BottomButtonsInitializer(historyDisplayer, businessComponent, dictionaryComponent);
		bottomInitializer.init();
	}

}
