package com.megadict.initializer;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.recommending.WordListTask;
import com.megadict.business.recommending.WordListTask.OnClickWordListener;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;
import com.megadict.widget.ResultView.OnSelectTextListener;

public final class ResultViewInitializer implements Initializer {
	private final Activity activity;
	private final BusinessComponent businessComponent;
	private final DictionaryComponent dictionaryComponent;

	public ResultViewInitializer(final Activity activity,
			final BusinessComponent businessComponent,
			final DictionaryComponent dictionaryComponent) {
		this.activity = activity;
		this.businessComponent = businessComponent;
		this.dictionaryComponent = dictionaryComponent;
		initResultView();
	}

	public void initResultView() {
		// Prepare components.
		final ClipboardManager clipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
		final ResultView resultView = dictionaryComponent.getResultView();
		final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();

		resultView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		resultView.setBackgroundColor(0x00000000);
		resultView.setOnSelectTextListener(new OnSelectTextListener() {
			@Override
			public void onSelectText() {
				final String text = clipboardManager.getText().toString();
				final WordListTask task = new WordListTask(activity, text);
				task.setOnClickWordListener(new OnClickWordListener() {
					@Override
					public void onClickWord() {
						searchBar.setText(task.getWord());
						doSearching(searchBar.getText().toString());
					}
				});
				task.execute((Void [])null);
			}
		});
	}

	private void doSearching(final String word) {
		// THE OUTER IF MAKES SURE THAT NO CRASH IN MEGADICT.
		/// I'M NOT SATISFIED WITH THIS BECAUSE THE DICTIONARY MODEL CAN'T BE USED BY MULTIPLE THREADS.
		/// IT MEANS THAT WHEN RECOMMENDING IS RUNNING,
		/// WE CAN'T RUN ANOTHER THREAD TO SEARCH WORD ON THE SAME DICTIONARY MODEL.
		/// NEED TO FIX THE DICTIONARY MODEL.
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
	public void doNothing() { /* Empty for no reason, ok? */ }
}
