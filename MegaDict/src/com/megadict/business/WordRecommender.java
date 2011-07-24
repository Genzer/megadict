package com.megadict.business;

import java.util.List;

import com.megadict.model.Dictionary;
import com.megadict.thread.RecommendThread;

public class WordRecommender {
	private final List<Dictionary> dictionaryModels;

	public WordRecommender(final List<Dictionary> dictionaryModels) {
		this.dictionaryModels = dictionaryModels;
	}

	public List<String> recommend(final String word) {
		final RecommendThread thread = new RecommendThread(dictionaryModels, word);
		thread.start();
		thread.join();
		return thread.getRecommendWords();
	}
}
