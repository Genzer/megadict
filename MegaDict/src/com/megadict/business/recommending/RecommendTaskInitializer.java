package com.megadict.business.recommending;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.utility.MegaLogger;

public class RecommendTaskInitializer {
	// Recommend tasks' variables.
	private static final int MAX_RECOMMENDED_WORD_COUNT = 300;
	private final SortedSet<String> recommendWords = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	private final WordRecommender recommender;
	private final DictionaryComponent dictionaryComponent;

	public RecommendTaskInitializer(final WordRecommender recommender, final DictionaryComponent dictionaryComponent) {
		this.recommender = recommender;
		this.dictionaryComponent = dictionaryComponent;
	}

	public void setOnPreExecuteListener(final RecommendTask task) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (recommender.didAllRecommendTasksFinish()) {
					recommendWords.clear();
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	public void setOnPostExecuteListener(final RecommendTask task) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<List<String>>() {
			@Override
			public void onPostExecute(final List<String> list) {
				recommendWords.addAll(list);
				if (recommender.didAllRecommendTasksFinish()) {
					final List<String> tempList = Arrays.asList(recommendWords.toArray(new String[recommendWords.size()]));
					final List<String> adaptedList = tempList.subList(0, Math.min(MAX_RECOMMENDED_WORD_COUNT, tempList.size()));
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(dictionaryComponent.getContext(), R.layout.dropdown_item, adaptedList);

					final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();
					searchBar.setAdapter(adapter);
					// This try-catch prevent unexpected errors when orientation changes.
					try {
						// Show dropdown list.
						searchBar.showDropDown();
						// Hide ProgressBar.
						final ProgressBar proressBar = dictionaryComponent.getProgressBar();
						proressBar.setVisibility(ProgressBar.INVISIBLE);
					} catch (final Exception e) {
						MegaLogger.log(e.getMessage());
					}
				}
			}
		});
	}
}
