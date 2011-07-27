package com.megadict.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.megadict.exception.RecommendingException;
import com.megadict.model.Dictionary;

public class RecommendThread implements Runnable {
	private final int RECOMMENDED_WORD_COUNT = 10;
	private final List<Dictionary> dictionaryModels;
	private final Thread thread;
	private final String word;
	private List<String> recommendWords = new ArrayList<String>();

	public RecommendThread(final List<Dictionary> dictionaryModels, final String word) {
		this.dictionaryModels = dictionaryModels;
		this.word = word;
		thread = new Thread(this);
	}

	public void join() {
		try {
			thread.join();
		} catch (final InterruptedException e) {
			throw new RecommendingException(e);
		}
	}

	public void start() {
		thread.start();
	}

	@Override
	public void run() {
		final List<String> tempList = new ArrayList<String>();
		for(final Dictionary dict : dictionaryModels) {
			final List<String> words = dict.recommendWord(word);
			tempList.addAll(words);
		}
		Collections.sort(recommendWords);
		recommendWords = recommendWords.subList(0, Math.min(RECOMMENDED_WORD_COUNT, tempList.size()));
	}

	public List<String> getRecommendWords() {
		return recommendWords;
	}
}
