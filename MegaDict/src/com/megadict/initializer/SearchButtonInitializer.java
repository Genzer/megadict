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

public class SearchButtonInitializer {
	public static void init(final Activity activity,
			final BusinessComponent component,
			final DictionaryComponent dictionaryComponent) {
		final Button searchButton = dictionaryComponent.getSearchButton();
		final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				doSearching(activity, component, searchBar.getText().toString(), dictionaryComponent);
			}
		});
	}

	private static void doSearching(final Activity activity,
			final BusinessComponent component,
			final String word,
			final DictionaryComponent dictionaryComponent) {
		// THE OUTER IF MAKES SURE THAT NO CRASH IN MEGADICT.
		/// I'M NOT SATISFIED WITH THIS BECAUSE THE DICTIONARY MODEL CAN'T BE USED BY MULTIPLE THREADS.
		/// IT MEANS THAT WHEN RECOMMENDING IS RUNNING,
		/// WE CAN'T RUN ANOTHER THREAD TO SEARCH WORD ON THE SAME DICTIONARY MODEL.
		/// NEED TO FIX THE DICTIONARY MODEL.

		// Get useful components.
		final WordRecommender recommender = component.getRecommender();
		final WordSearcher searcher = component.getSearcher();

		if(recommender.isRecommending()) {
			Utility.messageBox(activity, activity.getString(R.string.recommending));
		} else {
			if(!searcher.lookup(word)) {
				Utility.messageBox(activity, activity.getString(R.string.searching));
			}
		}
	}
}
