package com.megadict.business.searching;

import android.os.AsyncTask;

import com.megadict.business.Workable;

public abstract class AbstractSearchTask extends AsyncTask<String, Void, String> implements Workable {
	private boolean working;

	@Override
	protected void onPreExecute() {
		working = true;
	}

	@Override
	protected abstract String doInBackground(final String... params);

	@Override
	protected void onPostExecute(final String result) {
		working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
