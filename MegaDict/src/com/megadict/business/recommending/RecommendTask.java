package com.megadict.business.recommending;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.megadict.bean.DictionaryComponent;
import com.megadict.model.Dictionary;

public class RecommendTask extends AbstractRecommendTask {
	private final static int RECOMMENDED_WORD_COUNT = 50;
	private final WordRecommender recommender;
	private final Dictionary model;
	private final DictionaryComponent dictionaryComponent;
	private static SortedSet<String> recommendWords = new TreeSet<String>();
	private boolean cancelled = false;

	public RecommendTask(final WordRecommender recommender, final Dictionary model, final DictionaryComponent dictionaryComponent) {
		super();
		this.recommender = recommender;
		this.model = model;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected void onCancelled() {
		cancelled = true;
	}

	@Override
	protected void onPreExecute() {
		if(recommender.didAllRecommendTasksFinish()) {
			recommendWords.clear();
			dictionaryComponent.getProgressBar().setVisibility(View.VISIBLE);
		}
		super.onPreExecute();
	}

	@Override
	protected List<String> doInBackground(final String... params) {
		while(!cancelled) {
			return model.recommendWord(params[0]);
		}
		return null;
	}

	@Override
	protected void onPostExecute(final List<String> list) {
		super.onPostExecute(list);
		if(list == null) return;

		recommendWords.addAll(list);
		if(recommender.didAllRecommendTasksFinish()) {
			final List<String> adaptedList = new ArrayList<String>();
			int i = 0;
			for(final String s : recommendWords) {
				if(i == RECOMMENDED_WORD_COUNT) break;
				++i;
				adaptedList.add(s);
			}

			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					dictionaryComponent.getContext(),
					android.R.layout.simple_dropdown_item_1line, adaptedList);
			dictionaryComponent.getSearchBar().setAdapter(adapter);
			// Show dropdown list.
			dictionaryComponent.getSearchBar().showDropDown();
			// Hide ProgressBar.
			dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
		}
	}
}
