package com.megadict.initializer;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.adapter.TextWatcherAdapter;
import com.megadict.bean.RecommendComponent;
import com.megadict.bean.SearchComponent;
import com.megadict.business.DictionaryClient;
import com.megadict.utility.Utility;

public class SearchBarInitializer {
	// Aggregation variables.
	private final Activity activity;
	private final DictionaryClient dictionaryClient;
	private final AutoCompleteTextView searchBar;

	// Composition variables.
	private final SearchComponent searchComponent;
	private final RecommendComponent recommendComponent;

	// Member variables.
	private long time;
	private boolean itemSelected;

	public SearchBarInitializer(final Activity activity, final DictionaryClient dictionaryClient,
			final SearchComponent searchComponent, final RecommendComponent recommendComponent) {
		this.activity = activity;
		this.dictionaryClient = dictionaryClient;
		this.searchBar = recommendComponent.searchBar;
		this.searchComponent = searchComponent;
		this.recommendComponent = recommendComponent;
		initSearchBar();
	}

	private void initSearchBar() {
		setOnEditorActionListener();
		addTextChangedListener();
		setOnItemClickListener();
		searchBar.setThreshold(1);

		// Disable soft keyboard.
		Utility.disableSoftKeyboard(activity, searchBar);
	}

	private void setOnEditorActionListener() {
		searchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					doSearching(searchBar.getText().toString());
				}
				return true;
			}
		});
	}

	private void addTextChangedListener() {
		searchBar.addTextChangedListener(new TextWatcherAdapter() {
			@Override
			public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
				if(itemSelected) {
					itemSelected = false;
					return;
				}
				final long diff = calculateIntervalBetweenTwoActions();
				if(diff > 500) {
					doRecommendWords(dictionaryClient, searchBar.getText().toString());
				}
			}
		});
	}

	private void setOnItemClickListener() {
		searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				itemSelected = true;
			}
		});
	}

	private void doSearching(final String word) {
		// THE OUTER IF MAKES SURE THAT NO CRASH IN MEGADICT.
		/// I'M NOT SATISFIED WITH THIS BECAUSE THE DICTIONARY MODEL CAN'T BE USED BY MULTIPLE THREADS.
		/// IT MEANS THAT WHEN RECOMMENDING IS RUNNING,
		/// WE CAN'T RUN ANOTHER THREAD TO SEARCH WORD ON THE SAME DICTIONARY MODEL.
		/// NEED TO FIX THE DICTIONARY MODEL.
		if(dictionaryClient.isRecommending()) {
			Utility.messageBox(activity, activity.getString(R.string.recommending));
		} else {
			if(!dictionaryClient.lookup(word, searchComponent)) {
				Utility.messageBox(activity, activity.getString(R.string.searching));
			}
		}
	}

	private void doRecommendWords(final DictionaryClient dictionaryClient, final String word) {
		if(!dictionaryClient.recommend(activity, word, recommendComponent)) {}
	}

	private long calculateIntervalBetweenTwoActions() {
		if(time == 0) {
			time = System.currentTimeMillis();
		}
		final long currentTime = System.currentTimeMillis();
		final long diff = currentTime - time;
		time = currentTime;
		return diff;
	}
}
