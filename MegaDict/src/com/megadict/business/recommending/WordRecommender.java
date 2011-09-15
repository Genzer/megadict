package com.megadict.business.recommending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.initializer.AbstractInitializer;
import com.megadict.model.Dictionary;

public final class WordRecommender implements Observer, RecommendTaskManager {
	private static final String TAG = "WordRecommender";
	private static final int DELAY_TIME = 1000;
	private static final int MAX_RECOMMENDED_WORD_COUNT = 300;

	// Aggregation variables.
	private final List<Dictionary> dictionaryModels;
	private DictionaryComponent dictionaryComponent;
	private final Context context;

	// Composition variables.
	private final List<RecommendTask> recommendTasks =
			new ArrayList<RecommendTask>();
	private final Handler recommendHandler = new RecommendHandler();
	private Runnable recommendRunnable;
	private final SortedSet<String> recommendWords = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	public WordRecommender(final Context context, final List<Dictionary> dictionaryModels, final DictionaryComponent dictionaryComponent) {
		this.context = context;
		this.dictionaryModels = dictionaryModels;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	public boolean didAllRecommendTasksFinish() {
		for (final RecommendTask task : recommendTasks) {
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
				final RecommendTask task = RecommendTask.create(model);
				setOnPreExecuteListener(task);
				setOnPostExecuteListener(task);
				task.execute(word);
				recommendTasks.add(task);
			}
		}
	}

	@Override
	public void cancelRecommending() {
		for (final RecommendTask task : recommendTasks) {
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
			// Should prevent recommending?
			if(arg instanceof Boolean) {
				preventRecommending();
			}

			// Should recommending?
			if (arg instanceof String) {
				// Remove older runnable (if there is any)
				recommendHandler.removeCallbacks(recommendRunnable);
				final String word = arg.toString();
				// Check if a Runnable should be posted.
				if (!"".equals(word)) {
					recommendRunnable = new RecommendRunnable(word);
					recommendHandler.postDelayed(recommendRunnable, DELAY_TIME);
				}
			}
		}
	}

	private void preventRecommending() {
		// Remove old runnable in handler.
		recommendHandler.removeCallbacks(recommendRunnable);
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

	public void setOnPreExecuteListener(final RecommendTask task) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllRecommendTasksFinish()) {
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
				if(dictionaryComponent == null) return;

				recommendWords.addAll(list);
				if (didAllRecommendTasksFinish()) {
					final List<String> tempList = Arrays.asList(recommendWords.toArray(new String[recommendWords.size()]));
					final List<String> adaptedList = tempList.subList(0, Math.min(MAX_RECOMMENDED_WORD_COUNT, tempList.size()));
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.dropdown_item, adaptedList);

					final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();
					searchBar.setAdapter(adapter);
					// This try-catch prevent unexpected errors when orientation changes.
					try {
						// Show dropdown list.
						searchBar.showDropDown();
						// Hide ProgressBar.
						final ProgressBar proressBar = dictionaryComponent.getProgressBar();
						proressBar.setVisibility(ProgressBar.INVISIBLE);
					} catch (final Exception e) {
						Log.w(TAG, e.getMessage(), e);
					}
				}
			}
		});
	}

	public void setDictionaryComponent(final DictionaryComponent dictionaryComponent) {
		this.dictionaryComponent = dictionaryComponent;
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
