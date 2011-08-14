package com.megadict.initializer;

import java.util.Observable;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;

public class SearchBarInitializer extends Observable implements Initializer {
	private final Activity activity;
	private final BusinessComponent businessComponent;
	private final DictionaryComponent dictionaryComponent;

	public SearchBarInitializer(final Activity activity,
			final BusinessComponent businessComponent,
			final DictionaryComponent dictionaryComponent) {
		super();
		this.activity = activity;
		this.businessComponent = businessComponent;
		this.dictionaryComponent = dictionaryComponent;
		initSearchBar();
	}

	private void initSearchBar() {
		// Prepare components.
		final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();
		searchBar.setThreshold(1);

		// Set listeners.
		setOnEditorActionListener(searchBar);
		addTextChangedListener(searchBar);
		setOnItemClickListener(searchBar);

		// Disable soft keyboard.
		Utility.disableSoftKeyboard(activity, searchBar);
	}

	private void addTextChangedListener(final AutoCompleteTextView searchBar) {
		searchBar.addTextChangedListener(new SearchBarTextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				textChanged(s);
			}

			private void textChanged(final CharSequence s) {
				setChanged();
				notifyObservers(s.toString());
			}
		});
	}

	private void setOnItemClickListener(final AutoCompleteTextView searchBar) {
		searchBar.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				preventRecommending();
				doSearching(searchBar.getText().toString());
			}
		});
	}

	private void setOnEditorActionListener(final AutoCompleteTextView searchBar) {
		searchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					preventRecommending();
					doSearching(searchBar.getText().toString());
				}
				return true;
			}
		});
	}

	private void doSearching(final String word) {
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

	private void preventRecommending() {
		setChanged();
		notifyObservers(true);
	}

	private class SearchBarTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) { /* Empty for no reason, ok? */ }
		@Override
		public void onTextChanged(final CharSequence s, final int start, final int before, final int count) { /* Empty for no reason, ok? */ }
		@Override
		public void afterTextChanged(final Editable s) { /* Empty for no reason, ok? */ }
	}

	@Override
	public void doNothing() { /* Empty for no reason, ok? */ }
}
