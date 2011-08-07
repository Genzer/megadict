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

public final class SearchBarInitializer {
	private static long time;
	private static boolean itemSelected;

	private SearchBarInitializer() {}

	public static void init(final Activity activity, final DictionaryClient dictionaryClient,
			final AutoCompleteTextView searchBar, final SearchComponent searchComponent,
			final RecommendComponent recommendComponent) {
		setOnEditorActionListener(activity, dictionaryClient, searchBar, searchComponent);
		addTextChangedListener(activity, dictionaryClient, searchBar, recommendComponent);
		setOnItemClickListener(searchBar);
		searchBar.setThreshold(1);

		// Disable soft keyboard.
		Utility.disableSoftKeyboard(activity, searchBar);
	}

	private static void setOnEditorActionListener(final Activity activity, final DictionaryClient dictionaryClient,
			final AutoCompleteTextView searchBar, final SearchComponent searchComponent) {
		searchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					doSearching(activity, dictionaryClient, searchBar.getText().toString(), searchComponent);
				}
				return true;
			}
		});
	}

	private static void addTextChangedListener(final Activity activity, final DictionaryClient dictionaryClient,
			final AutoCompleteTextView searchBar, final RecommendComponent recommendComponent) {
		searchBar.addTextChangedListener(new TextWatcherAdapter() {
			@Override
			public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
				if(itemSelected) {
					itemSelected = false;
					return;
				}
				final long diff = calculateIntervalBetweenTwoActions();
				if(diff > 500) {
					doRecommendWords(activity, dictionaryClient, searchBar.getText().toString(), recommendComponent);
				}
			}
		});
	}

	private static void setOnItemClickListener(final AutoCompleteTextView searchBar) {
		searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				itemSelected = true;
			}
		});
	}

	private static void doRecommendWords(final Activity activity, final DictionaryClient dictionaryClient, final String word, final RecommendComponent recommendComponent) {
		if(!dictionaryClient.recommend(activity, word, recommendComponent)) {}
	}

	private static long calculateIntervalBetweenTwoActions() {
		if(time == 0) {
			time = System.currentTimeMillis();
		}
		final long currentTime = System.currentTimeMillis();
		final long diff = currentTime - time;
		time = currentTime;
		return diff;
	}

	private static void doSearching(final Activity activity, final DictionaryClient dictionaryClient,
			final String word, final SearchComponent searchComponent) {
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
}
