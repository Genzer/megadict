package com.megadict.business.scanning;

import android.os.AsyncTask;

import com.megadict.bean.DictionaryBean;
import com.megadict.business.Workable;

public abstract class AbstractScanTask extends AsyncTask<DictionaryBean, Void, Void> implements Workable {
	private boolean working;

	@Override
	protected void onPreExecute() {
		working = true;
	}

	@Override
	protected abstract Void doInBackground(final DictionaryBean... params);

	@Override
	protected void onPostExecute(final Void result) {
		working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
