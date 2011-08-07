package com.megadict.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.megadict.bean.RecommendComponent;
import com.megadict.exception.RecommendingException;
import com.megadict.model.Dictionary;
import com.megadict.task.base.BaseRecommendTask;

public class RecommendTask extends BaseRecommendTask {
	private final int RECOMMENDED_WORD_COUNT = 10;
	private final int TIMEOUT_IN_SECONDS = 3;
	private final Context context;
	private final List<Dictionary> dictionaryModels;
	private final RecommendComponent recommendComponent;
	private boolean recommending;

	public RecommendTask(final Context context, final List<Dictionary> dictionaryModels, final RecommendComponent recommendComponent) {
		this.context = context;
		this.dictionaryModels = dictionaryModels;
		this.recommendComponent = recommendComponent;
	}

	@Override
	protected void onPreExecute() {
		recommendComponent.progressBar.setVisibility(View.VISIBLE);
		recommending = true;
	}

	@Override
	protected List<String> doInBackground(final String... params) {
		final String word = params[0];

		// Create callable list.
		final List<Callable<List<String>>> callables = new ArrayList<Callable<List<String>>>();
		for(final Dictionary model : dictionaryModels) {
			final RecommendThread thread = new RecommendThread(word, model);
			callables.add(thread);
		}

		// Invoke callables.
		final SortedSet<String> tempList = new TreeSet<String>();
		try {
			// Create thread pool.
			final ExecutorService service = Executors.newFixedThreadPool(Math.max(1, dictionaryModels.size()));
			// Invoke all callables.
			final List<Future<List<String>>> futures = service.invokeAll(callables);
			service.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			// Add all recommend words of every dictionaries to a sorted set.
			for(final Future<List<String>> future : futures) {
				final List<String> words = future.get();
				tempList.addAll(words);
			}
			service.shutdown();
		} catch (final InterruptedException e) {
			throw new RecommendingException(e);
		} catch (final ExecutionException e) {
			throw new RecommendingException(e);
		}

		final int neededWordCount = Math.min(RECOMMENDED_WORD_COUNT, tempList.size());
		// Get a number of needed recommended words from the set.
		final List<String> recommendWords = Arrays.asList(tempList.toArray(new String[neededWordCount]));
		return recommendWords.subList(0, neededWordCount);
	}

	@Override
	protected void onPostExecute(final List<String> list) {
		//		System.out.println("=====================");
		//		for(final String s : list) {
		//			System.out.println(s);
		//		}
		//final String []a = {"w", "word", "wobble", "wit", "work", "why", "wet"};
		recommendComponent.searchBar.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, list));
		recommendComponent.searchBar.showDropDown();
		recommendComponent.progressBar.setVisibility(View.INVISIBLE);
		recommending = false;
	}

	public boolean isRecommeding() {
		return recommending;
	}

	private class RecommendThread implements Callable<List<String>> {
		private final String word;
		private final Dictionary dictionary;

		public RecommendThread(final String word, final Dictionary dictionary) {
			this.word = word;
			this.dictionary = dictionary;
		}

		@Override
		public List<String> call() throws Exception {
			return dictionary.recommendWord(word);
		}
	}
}
