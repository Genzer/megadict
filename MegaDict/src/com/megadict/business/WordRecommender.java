package com.megadict.business;

import java.util.List;

import com.megadict.model.Dictionary;
import com.megadict.thread.RecommendThread;

public class WordRecommender {
	public List<String> recommend(final String word, final List<Dictionary> dictionaryModels) {
		final RecommendThread thread = new RecommendThread(dictionaryModels, word);
		thread.start();
		thread.join();
		return thread.getRecommendWords();
	}
}
