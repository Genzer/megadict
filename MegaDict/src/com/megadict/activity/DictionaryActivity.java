package com.megadict.activity;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.activity.base.BaseActivity;
import com.megadict.application.MegaDictApp;
import com.megadict.business.DictionaryClient;
import com.megadict.business.ResultTextMaker;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.listener.OnTextChangeListener;
import com.megadict.task.RecommendTask;
import com.megadict.task.SearchTask;
import com.megadict.task.WordListTask;
import com.megadict.task.WordListTask.OnClickWordListener;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;
import com.megadict.widget.ResultView.OnSelectTextListener;

public class DictionaryActivity extends BaseActivity implements OnClickListener, OnEditorActionListener, OnSelectTextListener {
	private final String TAG = "DictionaryActivity";

	// Activity control variables.
	private AutoCompleteTextView searchEditText;
	private ResultView resultView;
	private ProgressBar progressBar;
	private ClipboardManager clipboardManager;

	// Member variables
	private DictionaryClient dictionaryClient;
	private SQLiteDatabase database;
	private ResultTextMaker resultTextMaker;
	private SearchTask searchTask;
	private RecommendTask recommendTask;
	private WordListTask task;
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
		if(shouldSetStartPage) {
			setStartPage();
		}
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
		} else if(item.getItemId() == R.id.selectTextMenuItem) {
			selectText(resultView);
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
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		searchButton.setOnEditorActionListener(this);
		searchEditText = (AutoCompleteTextView) findViewById(R.id.searchEditText);
		searchEditText.addTextChangedListener(new OnTextChangeListener() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				//doRecommendWords(searchEditText.getText().toString());
			}
		});
		// Init Result view.
		resultView = new ResultView(this, clipboardManager);
		resultView.setBackgroundColor(0x00000000);
		resultView.setOnSelectTextListener(this);
		final ScrollView resultScrollView = (ScrollView)findViewById(R.id.resultScrollView);
		resultScrollView.addView(resultView);
	}

	private void doRecommendWords(final String word) {
		if(recommendTask != null) {
			if(recommendTask.isCancelled()) {
				recommendTask.cancel(true);
			}
			recommendTask = new RecommendTask(this, dictionaryClient, progressBar, searchEditText);
			recommendTask.execute(word);
		}
	}

	private void initVariables() {
		// Get application-scoped variables.
		dictionaryClient = ((MegaDictApp) getApplication()).dictionaryClient;
		// Create and open database.
		final DatabaseHelper helper = new DatabaseHelper(this);
		database = helper.getReadableDatabase();
		// Scan chosen databases when MegaDict opens.
		doScanningDatabase(this, database);
		// Init result text maker.
		resultTextMaker = new ResultTextMaker(getAssets());
		// Init clipboard manager.
		clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	}

	private void doSearching(final String word) {
		if(searchTask == null || !searchTask.isSearching()) {
			searchTask = new SearchTask(dictionaryClient, resultTextMaker,
					progressBar, resultView, getString(R.string.noDictionary));
			searchTask.execute(word);
			shouldSetStartPage = false;
		} else {
			Utility.messageBox(this, getString(R.string.searching));
		}
	}

	private void setStartPage() {
		final int dictCount = dictionaryClient.getDictionaryNames().size();
		final String welcomeStr = (dictCount > 1 ? getString(R.string.usingDictionaryPlural, dictCount) : getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = resultTextMaker.getWelcomeHTML(welcomeStr);
		resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}

	private void selectText(final ResultView resultView) {
		try
		{
			final KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(resultView);
			Utility.messageBox(this, getString(R.string.selectText));
		}
		catch (final Exception e) {
			Log.e(TAG, getString(R.string.canNotSelectText), e);
		}
	}

	private void doScanningDatabase(final Activity activity, final SQLiteDatabase database) {
		try {
			dictionaryClient.scanDatabase(activity, database);
		} catch (final IndexFileNotFoundException e) {
			e.printStackTrace();
		} catch (final DataFileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
