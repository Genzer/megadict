package com.megadict.singletask.base;

import java.util.List;

import android.os.AsyncTask;

import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;

public abstract class BaseScanTask extends AsyncTask<Void, Void, Void> {
	protected final DictionaryInformation info;
	protected final List<Dictionary> dictionaryModels;
	protected boolean scanning;

	public BaseScanTask(final DictionaryInformation info, final List<Dictionary> dictionaryModels) {
		super();
		this.info = info;
		this.dictionaryModels = dictionaryModels;
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
