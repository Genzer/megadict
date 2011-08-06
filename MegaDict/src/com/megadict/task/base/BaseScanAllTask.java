package com.megadict.task.base;

import java.util.List;

import android.os.AsyncTask;

import com.megadict.model.DictionaryInformation;

public abstract class BaseScanAllTask extends AsyncTask<Void, Void, Void> {
	final protected List<DictionaryInformation> infos;
	private boolean scanning;

	public BaseScanAllTask(final List<DictionaryInformation> infos) {
		this.infos = infos;
	}

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
