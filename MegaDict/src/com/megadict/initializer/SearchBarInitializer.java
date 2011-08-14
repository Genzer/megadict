package com.megadict.initializer;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;

public final class SearchBarInitializer {
	private SearchBarInitializer() {}

	public static void init(final Activity activity, final BusinessComponent businessComponent,
			final DictionaryComponent dictionaryComponent) {
		// Prepare components.
		final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();
		searchBar.setThreshold(1);

		// Set listeners.
		setOnEditorActionListener(activity, businessComponent, dictionaryComponent);

		// Disable soft keyboard.
		Utility.disableSoftKeyboard(activity, searchBar);
	}

	private static void setOnEditorActionListener(final Activity activity,
			final BusinessComponent businessComponent,
			final DictionaryComponent dictionaryComponent) {
		dictionaryComponent.getSearchBar().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					doSearching(activity, businessComponent, dictionaryComponent,
							dictionaryComponent.getSearchBar().getText().toString());
				}
				return true;
			}
		});
	}

	private static void doSearching(final Activity activity, final BusinessComponent businessComponent,
			final DictionaryComponent dictionaryComponent, final String word) {
		// THE OUTER IF MAKES SURE THAT NO CRASH IN MEGADICT.
		/// I'M NOT SATISFIED WITH THIS BECAUSE THE DICTIONARY MODEL CAN'T BE USED BY MULTIPLE THREADS.
		/// IT MEANS THAT WHEN RECOMMENDING IS RUNNING,
		/// WE CAN'T RUN ANOTHER THREAD TO SEARCH WORD ON THE SAME DICTIONARY MODEL.
		/// NEED TO FIX THE DICTIONARY MODEL.

		// Get useful components.
		final WordSearcher searcher = businessComponent.getSearcher();
		final WordRecommender recommender = businessComponent.getRecommender();

		if(recommender.isRecommending()) {
			Utility.messageBox(activity, activity.getString(R.string.recommending));
		} else {
			if(!searcher.lookup(word)) {
				Utility.messageBox(activity, activity.getString(R.string.searching));
			}
		}
	}





}
