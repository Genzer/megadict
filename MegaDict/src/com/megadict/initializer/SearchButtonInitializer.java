package com.megadict.initializer;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;

public class SearchButtonInitializer implements Initializer {
	private final Activity activity;
	private final BusinessComponent businessComponent;
	private final DictionaryComponent dictionaryComponent;

	public SearchButtonInitializer(final Activity activity,
			final BusinessComponent businessComponent,
			final DictionaryComponent dictionaryComponent) {
		this.activity = activity;
		this.businessComponent = businessComponent;
		this.dictionaryComponent = dictionaryComponent;
		initSearchButton();
	}

	private void initSearchButton() {
		final Button searchButton = dictionaryComponent.getSearchButton();
		final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				doSearching(searchBar.getText().toString());
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
		final WordRecommender recommender = businessComponent.getRecommender();
		final WordSearcher searcher = businessComponent.getSearcher();

		if(recommender.isRecommending()) {
			Utility.messageBox(activity, activity.getString(R.string.recommending));
		} else {
			if(!searcher.lookup(word)) {
				Utility.messageBox(activity, activity.getString(R.string.searching));
			}
		}
	}

	@Override
	public void doNothing() {/* Empty for no reason, ok? */ }
}
