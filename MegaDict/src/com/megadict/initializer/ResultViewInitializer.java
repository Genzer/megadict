package com.megadict.initializer;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;

import com.megadict.R;
import com.megadict.bean.SearchComponent;
import com.megadict.business.DictionaryClient;
import com.megadict.task.WordListTask;
import com.megadict.task.WordListTask.OnClickWordListener;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView.OnSelectTextListener;

public final class ResultViewInitializer {
	private ResultViewInitializer() {}

	public static void init(final Activity activity, final DictionaryClient dictionaryClient,
			final AutoCompleteTextView searchBar, final SearchComponent searchComponent) {
		final ClipboardManager clipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
		searchComponent.resultView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		searchComponent.resultView.setBackgroundColor(0x00000000);

		searchComponent.resultView.setOnSelectTextListener(new OnSelectTextListener() {
			@Override
			public void onSelectText() {
				final String text = clipboardManager.getText().toString();
				final WordListTask task = new WordListTask(activity, text);
				task.setOnClickWordListener(new OnClickWordListener() {
					@Override
					public void onClickWord() {
						searchBar.setText(task.getWord());
						doSearching(activity, dictionaryClient, searchBar.getText().toString(), searchComponent);
					}
				});
				task.execute((Void [])null);
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
