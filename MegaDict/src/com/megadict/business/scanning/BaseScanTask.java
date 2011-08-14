package com.megadict.business.scanning;

import android.os.AsyncTask;

public abstract class BaseScanTask extends AsyncTask<Void, Void, Void> {
	private boolean scanning;

	@Override
	protected void onPreExecute() {
		scanning = true;
	}

	@Override
	protected abstract Void doInBackground(final Void... params);

	@Override
	protected void onPostExecute(final Void result) {
		scanning = false;
	}

	public boolean isScanning() {
		return scanning;
	}
}
