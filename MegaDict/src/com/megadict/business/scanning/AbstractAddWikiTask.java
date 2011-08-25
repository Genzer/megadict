package com.megadict.business.scanning;

import com.megadict.business.Workable;

import android.os.AsyncTask;

public abstract class AbstractAddWikiTask extends AsyncTask<String, Void, Void> implements Workable {
	private boolean working;

	@Override
	protected void onPreExecute() {
		working = true;
	}

	@Override
	protected abstract Void doInBackground(final String... params);

	@Override
	protected void onPostExecute(final Void result) {
		working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
