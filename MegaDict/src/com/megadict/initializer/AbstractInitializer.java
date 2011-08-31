package com.megadict.initializer;

import java.util.Observable;

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

	// Composition variables.
	public AbstractInitializer(final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super();
		this.businessComponent = businessComponent;
		this.dictionaryComponent = dictionaryComponent;
		init();
	}

	protected abstract void init();

	protected void preventRecommending() {
		setChanged();
		notifyObservers(true);
	}

	protected void doSearching(final String word) {
		// THE OUTER IF MAKES SURE THAT NO CRASH IN MEGADICT.
		// / I'M NOT SATISFIED WITH THIS BECAUSE THE DICTIONARY MODEL CAN'T BE USED BY MULTIPLE
		// THREADS.
		// / IT MEANS THAT WHEN RECOMMENDING IS RUNNING,
		// / WE CAN'T RUN ANOTHER THREAD TO SEARCH WORD ON THE SAME DICTIONARY MODEL.
		// / NEED TO FIX THE DICTIONARY MODEL.

		// Get useful components.
		final WordSearcher searcher = businessComponent.getSearcher();
		final WordRecommender recommender = businessComponent.getRecommender();

		if (!recommender.didAllRecommendTasksFinish()) {
			recommender.cancelRecommending();
		}

		if (searcher.didAllSearchTasksFinish()) {
			searcher.search(word);
		} else {
			Utility.messageBox(dictionaryComponent.getContext(), R.string.searching);
		}

	}

	@Override
	public abstract void doNothing();
}
