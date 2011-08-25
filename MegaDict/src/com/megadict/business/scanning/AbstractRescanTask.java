package com.megadict.business.scanning;

import android.os.AsyncTask;

import com.megadict.business.Workable;
import com.megadict.model.DictionaryInformation;

public abstract class AbstractRescanTask extends AsyncTask<DictionaryInformation, Void, Void> implements Workable {
	private boolean working;

	@Override
	protected void onPreExecute() {
		working = true;
	}

	@Override
	protected abstract Void doInBackground(final DictionaryInformation... params);

	@Override
	protected void onPostExecute(final Void result) {
		working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
