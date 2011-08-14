package com.megadict.business.searching;

import android.os.AsyncTask;

public abstract class BaseSearchTask extends AsyncTask<String, Void, String>{
	private boolean searching;

	@Override
	protected void onPreExecute() {
		searching = true;
	}

	@Override
	protected abstract String doInBackground(final String... params);

	@Override
	protected void onPostExecute(final String result) {
		searching = false;
	}

	public boolean isSearching() {
		return searching;
	}
}
