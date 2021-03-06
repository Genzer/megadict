package com.megadict.initializer;

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

import com.megadict.business.DictionaryClient;
import com.megadict.utility.Utility;

public class SearchBarInitializer implements Initializer {
	private final DictionaryClient dictionaryClient;

	public SearchBarInitializer(final DictionaryClient dictionaryClient) {
		this.dictionaryClient = dictionaryClient;
	}

	@Override
	public void init() {
		// Prepare components.
		final AutoCompleteTextView searchBar =
				dictionaryClient.getSearchBar();

		// Set listeners.
		setOnEditorActionListener(searchBar);
		addTextChangedListener(searchBar);
		setOnItemClickListener(searchBar);

		// Disable soft keyboard.
		Utility.disableSoftKeyboard(dictionaryClient.getContext(), searchBar);
	}

	private void addTextChangedListener(final AutoCompleteTextView searchBar) {
		searchBar.addTextChangedListener(new SearchBarTextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				dictionaryClient.recommend();
			}
		});
	}

	private void setOnItemClickListener(final AutoCompleteTextView searchBar) {
		searchBar.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				dictionaryClient.search();
			}
		});
	}

	private void setOnEditorActionListener(final AutoCompleteTextView searchBar) {
		searchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_NEXT
						|| (event != null
						&& event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					dictionaryClient.search();
					return true;
				}
				return false;
			}
		});
	}

	private class SearchBarTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			/* Empty for no reason, ok? */
		}

		@Override
		public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			/* Empty for no reason, ok? */
		}

		@Override
		public void afterTextChanged(final Editable s) {
			/* Empty for no reason, ok? */
		}
	}
}
