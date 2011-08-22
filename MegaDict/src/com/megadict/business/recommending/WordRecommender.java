package com.megadict.business.recommending;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.initializer.AbstractInitializer;
import com.megadict.model.Dictionary;

public final class WordRecommender implements Observer {
	private final static int DELAY_TIME = 2000;
	private final List<Dictionary> dictionaryModels;
	private final DictionaryComponent dictionaryComponent;

	private RecommendTask recommendTask;
	private Runnable recommendRunnable;
	private final Handler recommendHandler = new RecommendHandler();

	public WordRecommender(final List<Dictionary> dictionaryModels, final DictionaryComponent dictionaryComponent) {
		this.dictionaryModels = dictionaryModels;
		this.dictionaryComponent = dictionaryComponent;
	}

	private boolean recommend(final String word) {
		boolean result;
		if(recommendTask == null || !recommendTask.isRecommending()) {
			// Lower and trim it.
			final String recommendedWord = word.toLowerCase(Locale.ENGLISH).trim();
			if("".equals(recommendedWord)) {
				/* Do nothing if recommendedWord is empty. */
			} else {
				if(doesContainLocalDictionary()) {
					recommendTask = new RecommendTask(dictionaryModels, dictionaryComponent);
					recommendTask.execute(recommendedWord);
				}
			}
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public boolean isRecommending() {
		return recommendTask == null ? false : recommendTask.isRecommending();
	}

	private boolean doesContainLocalDictionary() {
		for(final Dictionary dictionary : dictionaryModels) {
			if(dictionary instanceof DICTDictionary) {
				return true;
			}
		}
		return false;
	}

	private class RecommendHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			final String word = msg.getData().getString("word");
			recommend(word);
		}
	}

	//============= Inner class ===============//
	private class RecommendRunnable implements Runnable {
		private final String word;
		public RecommendRunnable(final String word) {
			this.word = word;
		}

		@Override
		public void run() {
			final Message msg = new Message();
			final Bundle bundle = new Bundle();
			bundle.putString("word", word);
			msg.setData(bundle);
			recommendHandler.sendMessage(msg);
		}
	}

	@Override
	public void update(final Observable o, final Object arg) {
		if(o instanceof DictionaryScanner) {
			updateModels(arg);
		} else if(o instanceof AbstractInitializer) {
			// Remove old runnable in handler.
			preventRecommending();

			// Should recommending?
			if(arg instanceof String) {
				// Check if a Runnable should be posted.
				if(recommendTask != null && recommendTask.isRecommending()) {
					// Can't postDelayed if a runnable was in runnable queue.
				} else {
					// postDelayed.
					recommendRunnable = new RecommendRunnable(arg.toString());
					recommendHandler.postDelayed(recommendRunnable, DELAY_TIME);
				}
			}
		}
	}

	private void preventRecommending() {
		if(recommendRunnable != null) {
			recommendHandler.removeCallbacks(recommendRunnable);
		}
		// Dismiss if drop down list presented.
		dictionaryComponent.getSearchBar().dismissDropDown();
	}

	private void updateModels(final Object arg) {
		@SuppressWarnings("unchecked")
		final List<Dictionary> models = (List<Dictionary>)(arg);
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}
}
