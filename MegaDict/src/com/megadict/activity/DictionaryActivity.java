package com.megadict.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.megadict.business.DictionaryClient;
import com.megadict.business.HistoryDisplayer;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.business.scanning.DictionaryScanner.OnAfterCompleteScanListener;
import com.megadict.business.scanning.DictionaryScanner.OnCompleteScanListener;
import com.megadict.business.searching.WordSearcher;
import com.megadict.initializer.BottomButtonsInitializer;
import com.megadict.initializer.PronounceButtonInitializer;
import com.megadict.initializer.ResultViewInitializer;
import com.megadict.initializer.SearchBarInitializer;
import com.megadict.initializer.SearchButtonInitializer;
import com.megadict.model.Dictionary;
import com.megadict.preferences.LanguagePreference;
import com.megadict.preferences.SpeakerPreference;
import com.megadict.utility.ActivityHelper;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public final class DictionaryActivity extends AbstractActivity {
	// Application-scope variables.
	private DictionaryScanner scanner;

	// Member variables
	private WordSearcher searcher;
	private WordRecommender recommender;
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
		// Remove the dictionary component from the recommender.
		searcher.setDictionaryComponent(null);
		// Remove the dictionary component from the searcher.
		recommender.setDictionaryComponent(null);

		// Retain business component.
		return businessComponent;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		//Debug.startMethodTracing("calc");
		super.onCreate(savedInstanceState);

		// Load language when first start app.
		Utility.updateLocale(this, LanguagePreference.newInstance(this).getLanguage());
		// Set this to update menus.
		languageChanged = true;

		// Init all UIs.
		initSomething();

		// Once UI was initialized, refresh all strings.
		refreshStrings();

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
		//Debug.stopMethodTracing();
		super.onDestroy();

		recommender.setDictionaryComponent(null);
		searcher.setDictionaryComponent(null);
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
			historyDisplayer.showHistoryDialog(searcher.getHistoryList());
		}
		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(requestCode == ActivityHelper.SETTING_REQUEST && resultCode == Activity.RESULT_OK) {
			if(data.getBooleanExtra(LanguagePreference.LANGUAGE_CHANGED, false)) {
				refreshStrings();
				// Set this to update menus.
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
			scanner.updateDictionaryModels();
		}
	}


	// ========================= Private functions ======================= //
	private void inflateMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
	}

	private void refreshStrings() {
		// Change noDefinition string.
		searcher.setNoDefinitionStr(getString(R.string.noDefinition));
		searcher.setNoDictionaryStr(getString(R.string.noDictionary));
		// Redraw search bar hint
		dictionaryComponent.getSearchBar().setHint(R.string.searchBarHint);
		// Redraw bottom buttons.
		final Set<Button> buttons = bottomButtonMap.keySet();
		for(final Button button : buttons) {
			button.setText(bottomButtonMap.get(button));
		}
	}

	// ==================== Init functions =================//
	private void initSomething() {
		// Must init UI first!
		initDictionaryComponent();

		// Init searcher and recommender.
		initBusinessComponent();

		// The application-scoped because scanner must set DictionaryComponent.
		initScanner();

		initInitializers();
	}

	private void initScanner() {
		// Get application variables.
		scanner = ((MegaDictApp) getApplication()).scanner;
		// Set dictionary component for global scanner.
		scanner.setDictionaryComponent(dictionaryComponent);
		// Set listeners.
		scanner.setOnCompleteScanListener(new OnCompleteScanListener() {
			@Override
			public void onCompleteScan(final List<Dictionary> dictionaryModels) {
				searcher.updateDictionaryModels(dictionaryModels);
				recommender.updateDictionaryModels(dictionaryModels);
			}
		});
		scanner.setOnAfterCompleteScanListener(new OnAfterCompleteScanListener() {
			@Override
			public void onAfterCompleteScan() {
				reportDictionaryCount();
			}
		});
	}

	private void reportDictionaryCount() {
		final int dictCount = scanner.getDictionaryModelCount();
		final String welcomeStr = (dictCount > 1
				? getString(R.string.usingDictionaryPlural, dictCount)
						: getString(R.string.usingDictionary, dictCount));
		Utility.messageBox(this, welcomeStr);
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
				bottomButtons(new ArrayList<Button>(bottomButtonMap.keySet())).build();
	}

	private void initBusinessComponent() {
		// Get saved instance.
		final BusinessComponent bc = (BusinessComponent)getLastNonConfigurationInstance();
		if(bc == null) {
			// Init searcher and recommender.
			recommender = new WordRecommender(this, dictionaryComponent);
			searcher = new WordSearcher(dictionaryComponent);
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
	}

	private void initInitializers() {
		final DictionaryClient dictionaryClient = new DictionaryClient(this, businessComponent, dictionaryComponent);

		// Init search button.
		new SearchButtonInitializer(dictionaryClient).init();

		// Init pronounce button.
		pronounceButtonInitializer = new PronounceButtonInitializer(this, dictionaryComponent);

		// Init search bar.
		new SearchBarInitializer(dictionaryClient).init();

		// Init Result view.
		final ResultViewInitializer resultViewInitializer = new ResultViewInitializer(dictionaryClient);
		resultViewInitializer.init();
		resultViewInitializer.loadWelcomeStr();

		// Init history displayer.
		historyDisplayer = new HistoryDisplayer(dictionaryClient);

		// Init bottom buttons.
		new BottomButtonsInitializer(historyDisplayer, dictionaryClient).init();
	}
}
