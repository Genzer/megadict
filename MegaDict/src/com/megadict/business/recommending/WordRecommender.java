package com.megadict.business.recommending;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

import com.megadict.bean.DictionaryComponent;
import com.megadict.model.Dictionary;

public final class WordRecommender implements Observer {
	private final int DELAY_TIME = 2000;
	private final List<Dictionary> dictionaryModels;
	private final DictionaryComponent dictionaryComponent;
	boolean begin;

	private RecommendTask recommendTask;
	Runnable recommendRunnable;
	private final Handler recommendHandler = new RecommendHandler();

	public WordRecommender(final List<Dictionary> dictionaryModels, final DictionaryComponent dictionaryComponent) {
		this.dictionaryModels = dictionaryModels;
		this.dictionaryComponent = dictionaryComponent;
		final AutoCompleteTextView searchBar = dictionaryComponent.getSearchBar();
		searchBar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				if(!begin) begin = true;
				if(begin) {
					if(recommendRunnable != null) {
						recommendHandler.removeCallbacks(recommendRunnable);
					}

					if(recommendTask != null && recommendTask.isRecommending()) {
						// Can't postDelayed if a runnable was in runnable queue.
					} else {
						recommendRunnable = new RecommendRunnable(s.toString());
						recommendHandler.postDelayed(recommendRunnable, DELAY_TIME);
					}
				}

			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
			}
		});

		searchBar.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				if(recommendRunnable != null) {
					recommendHandler.removeCallbacks(recommendRunnable);
				}
			}
		});
	}

	private boolean recommend(final String word) {
		if(recommendTask == null || !recommendTask.isRecommending()) {
			// Lower and trim it.
			final String recommendedWord = word.toLowerCase(Locale.ENGLISH).trim();
			// Return true if recommendWord empty.
			if(recommendedWord.equals("")) {
				return true;
			}

			recommendTask = new RecommendTask(this, dictionaryModels, dictionaryComponent);
			recommendTask.execute(recommendedWord);
			return true;
		}
		return false;
	}

	public boolean isRecommending() {
		if(recommendTask != null) {
			return recommendTask.isRecommending();
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
		@SuppressWarnings("unchecked")
		final List<Dictionary> models = (List<Dictionary>)(arg);
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}
}
