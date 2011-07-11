package com.megadict.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.application.MegaDictApp;
import com.megadict.business.DictionaryClient;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultTextView;

public class DictionaryActivity extends BaseActivity implements OnClickListener, OnEditorActionListener {
	private static final String TAG = "DictionaryActivity";
	private DictionaryClient dictionaryClient;
	private Button searchButton;
	private EditText searchEditText;
	private SQLiteDatabase database;
	private ProgressDialog progressDialog;
	private boolean isSearching = false;
	private ViewGroup resultPanel;
	private Typeface font;
	private List<String> dictionaryNames;

	public DictionaryActivity() {
		super(R.layout.search);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();

		// Init typeface.
		font = Typeface.createFromAsset(getAssets(), "fonts/DejaVuSans.ttf");

		// Init progress dialog
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Searching...");

		// Get application-scoped variables.
		dictionaryClient = ((MegaDictApp) getApplication()).dictionaryClient;

		// Init dictionary name list.
		dictionaryNames = dictionaryClient.getDictionaryNames();

		// Create and open database.
		final DatabaseHelper helper = new DatabaseHelper(this);
		database = helper.getReadableDatabase();

		// Scan chosen databases when MegaDict opens.
		dictionaryClient.scanDatabase(this, database);
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
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		searchButton.setOnEditorActionListener(this);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		resultPanel = (ViewGroup)findViewById(R.id.resultPanel);
	}

	private void doSearching(final String word) {
		if(!isSearching) {
			final SearchTask searchTask = new SearchTask();
			searchTask.execute(word);
		} else {
			Utility.messageBox(this, "Seaching... Please wait.");
		}
	}

	// ============================ Search task inner class =============== //
	private class SearchTask extends AsyncTask<String, Void, List<String>> {
		private ProgressBar progressBar;
		@Override
		protected void onPreExecute() {
			progressBar = (ProgressBar)findViewById(R.id.progressBar);
			progressBar.setVisibility(View.VISIBLE);
			isSearching = true;
		}

		@Override
		protected List<String> doInBackground(final String... params) {
			return search(params[0]);
		}

		@Override
		protected void onPostExecute(final List<String> list) {
			updateUI(list);
			progressBar.setVisibility(View.INVISIBLE);
			isSearching = false;
		}

		private List<String> search(final String word) {
			final List<String> contents = dictionaryClient.lookup(word, "");
			return contents;
		}

		private void updateUI(final List<String> contents) {
			resultPanel.removeAllViews();

			if(contents.size() == 0) {
				final TextView textView = createResultTextView(getString(R.string.noDictionary), Color.RED, "");
				resultPanel.addView(textView);
			} else {
				for(int i = 0; i < contents.size(); ++i) {
					final TextView titleTextView = createResultTextView(contents.get(i).trim(), Color.BLACK, dictionaryNames.get(i).trim());
					resultPanel.addView(titleTextView);
				}
			}
		}

		private ResultTextView createResultTextView(final String text, final int color, final String dictionaryName) {
			final ResultTextView resultTextView = new ResultTextView(DictionaryActivity.this);

			// Calculate px relative to dip.
			final int paddingInDIP = 5;
			final int marginInDIP = 5;
			final int paddingInPx = convertDIPToPx(resultTextView, paddingInDIP);
			final int marginInPx = convertDIPToPx(resultTextView, marginInDIP);
			// Set padding by px has just calculated.
			resultTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
			// Set text color and type face.
			resultTextView.setTextColor(Color.BLACK);
			resultTextView.setTypeface(font);
			// Add some bottom margin for each result view
			final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			param.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
			resultTextView.setLayoutParams(param);

			// Prepare result text.
			final String resultText = text + "\n\n" + dictionaryName;
			final SpannableString styledResultText = new SpannableString(resultText);
			styledResultText.setSpan(new ForegroundColorSpan(Color.GRAY), text.length() + 2, text.length() + 2 + dictionaryName.length(), 0);
			resultTextView.setText(styledResultText);
			return resultTextView;
		}

		private int convertDIPToPx(final View view, final int dip) {
			final float scale = view.getResources().getDisplayMetrics().density;
			return (int) (dip * scale + 0.5f);
		}
	}
}

