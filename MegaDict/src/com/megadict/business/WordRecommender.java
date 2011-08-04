package com.megadict.business;

import java.util.List;

import android.content.Context;

import com.megadict.bean.RecommendComponent;
import com.megadict.model.Dictionary;
import com.megadict.task.RecommendTask;

public class WordRecommender {
	private RecommendTask recommendTask;

	public boolean recommend(final Context context, final String word, final List<Dictionary> dictionaryModels, final RecommendComponent recommendComponent) {
		if(recommendTask == null || !recommendTask.isRecommeding()) {
			recommendTask = new RecommendTask(context, dictionaryModels, recommendComponent);
			recommendTask.execute(word);
			return true;
		}
		return false;
	}

	public boolean isRecommending() {
		if(recommendTask != null) {
			return recommendTask.isRecommeding();
		}
		return false;
	}
}
