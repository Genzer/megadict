package com.megadict.business.scanning;

import android.os.AsyncTask;

public abstract class AbstractScanTask extends AsyncTask<Void, Void, Void> implements Workable {
	private boolean working;

	@Override
	protected void onPreExecute() {
		working = true;
	}

	@Override
	protected abstract Void doInBackground(final Void... params);

	@Override
	protected void onPostExecute(final Void result) {
		working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
