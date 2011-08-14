package com.megadict.bean;

import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;

public class BusinessComponent {
	private final WordSearcher searcher;
	private final WordRecommender recommender;

	public BusinessComponent(final WordSearcher searcher, final WordRecommender recommender) {
		this.searcher = searcher;
		this.recommender = recommender;
	}

	public WordSearcher getSearcher() {
		return searcher;
	}

	public WordRecommender getRecommender() {
		return recommender;
	}
}
