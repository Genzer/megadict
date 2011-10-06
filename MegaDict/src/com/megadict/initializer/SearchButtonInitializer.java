package com.megadict.initializer;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.megadict.business.DictionaryClient;

public class SearchButtonInitializer implements Initializer {

	private final DictionaryClient dictionaryClient;
	public SearchButtonInitializer(final DictionaryClient dictionaryClient) {
		this.dictionaryClient = dictionaryClient;
	}

	@Override
	public void init() {
		final Button searchButton = dictionaryClient.getSearchButton();
		final AutoCompleteTextView searchBar =
				dictionaryClient.getSearchBar();
		setOnClickListener(searchButton, searchBar);
	}

	private void setOnClickListener(final Button searchButton, final AutoCompleteTextView searchBar) {
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				dictionaryClient.search();
			}
		});
	}
}
