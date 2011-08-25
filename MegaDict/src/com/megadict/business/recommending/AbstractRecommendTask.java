package com.megadict.business.recommending;

import java.util.List;

import android.os.AsyncTask;

import com.megadict.business.Workable;

public abstract class AbstractRecommendTask extends AsyncTask<String, Void, List<String>> implements Workable {
	protected boolean working;

	@Override
	protected void onPreExecute() {
		working = true;
	}

	@Override
	protected abstract List<String> doInBackground(final String... params);

	@Override
	protected void onPostExecute(final List<String> result) {
		working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
