package com.megadict.business.recommending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.model.Dictionary;

public final class WordRecommender {
	private static final int DELAY_TIME = 1500;
	private static final int MAX_RECOMMENDED_WORD_COUNT = 300;

	// Aggregation variables.
	private final List<Dictionary> dictionaryModels = new ArrayList<Dictionary>();
	private AutoCompleteTextView searchBar;
	private final ProgressBar progressBar;
	private final Context context;

	// Composition variables.
	private final List<RecommendTask> recommendTasks =
			new ArrayList<RecommendTask>();
	private final Handler recommendHandler = new RecommendHandler();
	private Runnable recommendRunnable;
	private final SortedSet<String> recommendWords = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	public WordRecommender(final Context context, final AutoCompleteTextView searchBar, final ProgressBar progressBar) {
		this.context = context;
		this.searchBar = searchBar;
		this.progressBar = progressBar;
	}

	public boolean didAllRecommendTasksFinish() {
		for (final RecommendTask task : recommendTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	public void cancelRecommending() {
		for (final RecommendTask task : recommendTasks) {
			task.cancel(true);
		}
		// Cancelling must clear old tasks.
		recommendTasks.clear();
	}

	public void setSearchBar(final AutoCompleteTextView searchBar) {
		this.searchBar = searchBar;
	}

	public void updateDictionaryModels(final List<Dictionary> models) {
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}

	public void recommend(final String word) {
		// Remove older runnable (if there is any)
		recommendHandler.removeCallbacks(recommendRunnable);
		// Check if a Runnable should be posted.
		if (!"".equals(word)) {
			recommendRunnable = new RecommendRunnable(word);
			recommendHandler.postDelayed(recommendRunnable, DELAY_TIME);
		}
	}

	public void preventRecommending() {
		// Remove old runnable in handler.
		recommendHandler.removeCallbacks(recommendRunnable);
	}

	private void setOnPreExecuteListener(final RecommendTask task) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllRecommendTasksFinish()) {
					recommendWords.clear();
					progressBar.setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	private void setOnPostExecuteListener(final RecommendTask task) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<List<String>>() {
			@Override
			public void onPostExecute(final List<String> list) {
				if(searchBar == null) return;

				recommendWords.addAll(list);
				if (didAllRecommendTasksFinish()) {
					final List<String> tempList = Arrays.asList(recommendWords.toArray(new String[recommendWords.size()]));
					final List<String> adaptedList = tempList.subList(0, Math.min(MAX_RECOMMENDED_WORD_COUNT, tempList.size()));
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.dropdown_item, adaptedList);
					searchBar.setAdapter(adapter);

					// Show dropdown list.
					searchBar.showDropDown();

					// Hide ProgressBar.
					progressBar.setVisibility(ProgressBar.INVISIBLE);
				}
			}
		});
	}

	private void recommendHelper(final String word) {
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

	// ============= Inner class ===============//
	private class RecommendHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			final String word = msg.getData().getString("word").trim().toLowerCase(Locale.US);
			recommendHelper(word);
		}
	}

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
