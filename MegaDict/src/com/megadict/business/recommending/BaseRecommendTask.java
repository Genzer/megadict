package com.megadict.business.recommending;

import java.util.List;

import android.os.AsyncTask;

public abstract class BaseRecommendTask extends AsyncTask<String, Void, List<String>> {
	private boolean recommending;

	@Override
	protected void onPreExecute() {
		recommending = true;
	}

	@Override
	protected abstract List<String> doInBackground(final String... params);

	@Override
	protected void onPostExecute(final List<String> result) {
		recommending = false;
	}

	public boolean isRecommending() {
		return recommending;
	}
}
