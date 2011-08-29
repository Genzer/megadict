package com.megadict.business.recommending;

import java.util.Collections;
import java.util.List;

import com.megadict.model.Dictionary;

public class RecommendTask extends AbstractRecommendTask {
	private final Dictionary model;
	private boolean cancelled = false;

	public RecommendTask(final Dictionary model) {
		super();
		this.model = model;
	}

	@Override
	protected void onCancelled() {
		cancelled = true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected List<String> doInBackground(final String... params) {
		while (!cancelled) {
			return model.recommendWord(params[0]);
		}
		return Collections.emptyList();
	}

	@Override
	protected void onPostExecute(final List<String> list) {
		super.onPostExecute(list);
	}
}
