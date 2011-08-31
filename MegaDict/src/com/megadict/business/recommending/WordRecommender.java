package com.megadict.business.recommending;

import java.util.ArrayList;
import java.util.List;
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

public final class WordRecommender implements Observer, RecommendTaskManager {
	private final static int DELAY_TIME = 2000;

	// Aggregation variables.
	private final List<Dictionary> dictionaryModels;
	private final DictionaryComponent dictionaryComponent;

	// Composition variables.
	private final RecommendTaskInitializer recommendTaskInitializer;
	private final List<AbstractRecommendTask> recommendTasks =
			new ArrayList<AbstractRecommendTask>();
	private final Handler recommendHandler = new RecommendHandler();
	private Runnable recommendRunnable;

	public WordRecommender(final List<Dictionary> dictionaryModels, final DictionaryComponent dictionaryComponent) {
		this.dictionaryModels = dictionaryModels;
		this.dictionaryComponent = dictionaryComponent;
		recommendTaskInitializer =
				new RecommendTaskInitializer(this, dictionaryComponent);
	}

	@Override
	public boolean didAllRecommendTasksFinish() {
		for (final AbstractRecommendTask task : recommendTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void recommend(final String word) {
		// Clear old tasks.
		recommendTasks.clear();
		// Create and execute tasks.
		for (final Dictionary model : dictionaryModels) {
			if (model instanceof DICTDictionary) {
				final AbstractRecommendTask task = new RecommendTask(model);
				recommendTaskInitializer.setOnPreExecuteListener(task);
				recommendTaskInitializer.setOnPostExecuteListener(task);
				task.execute(word);
				recommendTasks.add(task);
			}
		}
	}

	@Override
	public void cancelRecommending() {
		for (final AbstractRecommendTask task : recommendTasks) {
			task.cancel(true);
		}
		// Cancelling must clear old tasks.
		recommendTasks.clear();
	}

	@Override
	public void update(final Observable o, final Object arg) {
		if (o instanceof DictionaryScanner) {
			updateModels(arg);
		} else if (o instanceof AbstractInitializer) {
			// Remove old runnable in handler.
			preventRecommending();

			// Should recommending?
			if (arg instanceof String) {
				final String word = arg.toString();
				// Check if a Runnable should be posted.
				if (didAllRecommendTasksFinish() && !"".equals(word)) {
					//				if (!"".equals(word)) {
					// postDelayed.
					System.out.println("Post delayed");
					recommendRunnable = new RecommendRunnable(word);
					recommendHandler.postDelayed(recommendRunnable, DELAY_TIME);
				} else {
					// Can't postDelayed if a runnable was in runnable queue.
				}
			}
		}
	}

	private void preventRecommending() {
		if (recommendRunnable != null) {
			recommendHandler.removeCallbacks(recommendRunnable);
		}
		// Dismiss if drop down list presented.
		dictionaryComponent.getSearchBar().dismissDropDown();
	}

	private void updateModels(final Object arg) {
		@SuppressWarnings("unchecked")
		final List<Dictionary> models = (List<Dictionary>) (arg);
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}

	private class RecommendHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			final String word = msg.getData().getString("word");
			recommend(word);
		}
	}

	// ============= Inner class ===============//
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
}
