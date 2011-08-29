package com.megadict.initializer;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;

public class SearchButtonInitializer extends AbstractInitializer {

	public SearchButtonInitializer(final Context context, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(context, businessComponent, dictionaryComponent);
	}

	@Override
	protected void init() {
		final Button searchButton = dictionaryComponent.getSearchButton();
		final AutoCompleteTextView searchBar =
				dictionaryComponent.getSearchBar();
		setOnClickListener(searchButton, searchBar);
	}

	private void setOnClickListener(final Button searchButton, final AutoCompleteTextView searchBar) {
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				doSearching(searchBar.getText().toString());
				preventRecommending();
			}
		});
	}

	@Override
	public void doNothing() {/* Empty for no reason, ok? */
	}
}
