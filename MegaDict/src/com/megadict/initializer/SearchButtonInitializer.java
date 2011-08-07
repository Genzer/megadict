package com.megadict.initializer;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.megadict.R;
import com.megadict.bean.SearchComponent;
import com.megadict.business.DictionaryClient;
import com.megadict.utility.Utility;

public class SearchButtonInitializer {
	public static void init(final Activity activity, final DictionaryClient dictionaryClient,
			final Button searchButton, final AutoCompleteTextView searchBar,
			final SearchComponent searchComponent) {
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				doSearching(activity, dictionaryClient, searchBar.getText().toString(), searchComponent);
			}
		});
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
