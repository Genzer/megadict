package com.megadict.business.recommending;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;

public class RecommendTaskInitializer {
	// Recommend tasks' variables.
	private final static int RECOMMENDED_WORD_COUNT = 100;
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
					final List<String> adaptedList = new ArrayList<String>();
					int i = 0;
					for (final String s : recommendWords) {
						if (i == RECOMMENDED_WORD_COUNT) break;
						++i;
						adaptedList.add(s);
					}

					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(dictionaryComponent.getContext(), R.layout.dropdown_item, adaptedList);
					dictionaryComponent.getSearchBar().setAdapter(adapter);
					// Show dropdown list.
					dictionaryComponent.getSearchBar().showDropDown();
					// Hide ProgressBar.
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
				}
			}
		});
	}
}
