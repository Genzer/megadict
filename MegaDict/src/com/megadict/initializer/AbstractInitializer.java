package com.megadict.initializer;

import java.util.Observable;

import android.content.Context;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;

public abstract class AbstractInitializer extends Observable implements Initializer {
	// Aggregation variables.
	protected final BusinessComponent businessComponent;
	protected final DictionaryComponent dictionaryComponent;
	protected final Context context;

	// Composition variables.
	public AbstractInitializer(final Context context, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super();
		this.context = context;
		this.businessComponent = businessComponent;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	public abstract void init();

	protected void preventRecommending() {
		setChanged();
		notifyObservers(true);
	}

	/**
	 * This function does two jobs: Cancel the current RecommendTasks then search.
	 * @param word
	 */
	protected void doSearching(final String word) {
		// Get useful components.
		final WordSearcher searcher = businessComponent.getSearcher();
		final WordRecommender recommender = businessComponent.getRecommender();

		// Cancel recommending anyway if searching triggers!
		recommender.cancelRecommending();

		if (searcher.didAllSearchTasksFinish()) {
			searcher.search(word);
		} else {
			Utility.messageBox(context, R.string.searching);
		}
	}
}
