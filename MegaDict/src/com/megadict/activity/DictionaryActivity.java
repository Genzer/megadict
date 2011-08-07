package com.megadict.activity;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.activity.base.BaseActivity;
import com.megadict.adapter.TextWatcherAdapter;
import com.megadict.application.MegaDictApp;
import com.megadict.bean.RecommendComponent;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.bean.SearchComponent;
import com.megadict.business.DictionaryClient;
import com.megadict.business.DictionaryScanner;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.TextSelector;
import com.megadict.task.WordListTask;
import com.megadict.task.WordListTask.OnClickWordListener;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;
import com.megadict.widget.ResultView.OnSelectTextListener;

public class DictionaryActivity extends BaseActivity implements OnClickListener, OnSelectTextListener {
	private final String TAG = "DictionaryActivity";

	// Activity control variables.
	private AutoCompleteTextView searchEditText;
	public ResultView resultView;
	public ProgressBar progressBar;
	private ClipboardManager clipboardManager;

	// Member variables
	private DictionaryClient dictionaryClient;
	private SearchComponent searchComponent;
	private RecommendComponent recommendComponent;
	private ScanStorageComponent scanStorageComponent;
	private ResultTextMaker resultTextMaker;
	private SQLiteDatabase database;
	private WordListTask task;
	private TextSelector textSelector;
	private long time;
	private boolean itemSelected;
	private boolean shouldSetStartPage = true;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();
		initVariables();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(Utility.isLocaleChanged()) {
			// Change noDefinition string.
			dictionaryClient.setNoDefinitionString(getString(R.string.noDefinition));
		}
		//		if(shouldSetStartPage)
		//			setStartPage();
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

	@Override
	public void onClick(final View view) {
		if (view.getId() == R.id.searchButton) {
			doSearching(searchEditText.getText().toString());
			shouldSetStartPage = false;
		}
	}

	@Override
	public void onSelectText() {
		final String text = clipboardManager.getText().toString();
		task = new WordListTask(this, text);
		task.setOnClickWordListener(new OnClickWordListener() {
			@Override
			public void onClickWord() {
				searchEditText.setText(task.getWord());
				doSearching(searchEditText.getText().toString());
			}
		});
		task.execute((Void [])null);
	}

	// ========================= Private functions ======================= //
	private void initLayout() {
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		initSearchBar();
		// Init Result view.
		resultView = new ResultView(this, clipboardManager);
		resultView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		resultView.setBackgroundColor(0x00000000);
		resultView.setOnSelectTextListener(this);
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
		// Init result text maker.
		resultTextMaker = new ResultTextMaker(getAssets());
		// Init useful components.
		searchComponent = new SearchComponent(resultView, resultTextMaker, progressBar);
		recommendComponent = new RecommendComponent(searchEditText, resultView, progressBar);
		scanStorageComponent = new ScanStorageComponent(resultView, resultTextMaker, this);
		// Init clipboard manager.
		clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		// Init text selector.
		textSelector = new TextSelector();
		// Scan chosen databases when MegaDict opens.
		doScanningStorage();
	}

	private void initSearchBar() {
		searchEditText = (AutoCompleteTextView) findViewById(R.id.searchEditText);

		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					doSearching(searchEditText.getText().toString());
				}
				return true;
			}
		});
		searchEditText.setThreshold(1);
		searchEditText.addTextChangedListener(new TextWatcherAdapter() {

			@Override
			public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
				if(itemSelected) {
					itemSelected = false;
					return;
				}
				doRecommendWords(searchEditText.getText().toString());
				if(time == 0) {
					time = System.currentTimeMillis();
				}
				final long currentTime = System.currentTimeMillis();
				final long diff = currentTime - time;
				time = currentTime;
				if(diff > 500) {
					System.out.println("Recommending");
					doRecommendWords(searchEditText.getText().toString());
				}
			}
		});
		searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				itemSelected = true;
			}
		});

		// Disable soft keyboard.
		Utility.disableSoftKeyboard(this, searchEditText);
	}

	private void doRecommendWords(final String word) {
		if(!dictionaryClient.recommend(this, word, recommendComponent)) {}
	}

	public void doSearching(final String word) {
		// THE OUTER IF MAKES SURE THAT NO CRASH IN MEGADICT.
		/// I'M NOT SATISFIED WITH THIS BECAUSE THE DICTIONARY MODEL CAN'T BE USED BY MULTIPLE THREADS.
		/// IT MEANS THAT WHEN RECOMMENDING IS RUNNING,
		/// WE CAN'T RUN ANOTHER THREAD TO SEARCH WORD ON THE SAME DICTIONARY MODEL.
		/// NEED TO FIX THE DICTIONARY MODEL.
		if(!dictionaryClient.isRecommending()) {
			if(!dictionaryClient.lookup(word, searchComponent)) {
				Utility.messageBox(this, getString(R.string.searching));
			}
		} else {
			Utility.messageBox(this, getString(R.string.recommending));
		}
	}

	private void doScanningStorage() {
		if(!dictionaryClient.scanStorage(this, database, scanStorageComponent)) {
			Utility.messageBox(this, getString(R.string.scanning));
		}
	}

	private void setStartPage() {
		final int dictCount = DictionaryScanner.getDictionaryCount();
		System.out.println("Dict count: " + dictCount);
		try {
			final String welcomeStr = (dictCount > 1 ? scanStorageComponent.context.getString(R.string.usingDictionaryPlural, dictCount) : scanStorageComponent.context.getString(R.string.usingDictionary, dictCount));
			final String welcomeHTML = scanStorageComponent.resultTextMaker.getWelcomeHTML(welcomeStr);
			scanStorageComponent.resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
		} catch (final Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
