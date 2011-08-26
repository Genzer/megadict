package com.megadict.business.searching;

import android.os.AsyncTask;

import com.megadict.business.Workable;
import com.megadict.model.Definition;

public abstract class AbstractSearchTask extends AsyncTask<String, Void, Definition> implements Workable {
	protected OnPreExecuteListener onPreExecuteListener;
	protected OnPostExecuteListener onPostExecuteListener;
	protected boolean working;

	@Override
	protected void onPreExecute() {
		if(onPreExecuteListener != null) {
			onPreExecuteListener.onPreExecute();
		}
		working = true;
	}

	@Override
	protected abstract Definition doInBackground(final String... params);

	@Override
	protected void onPostExecute(final Definition definition) {
		working = false;
		if(onPostExecuteListener != null) {
			onPostExecuteListener.onPostExecute(definition);
		}
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	public void setOnPreExecuteListener(final OnPreExecuteListener onPreExecuteListener) {
		this.onPreExecuteListener = onPreExecuteListener;
	}

	public void setOnPostExecuteListener(final OnPostExecuteListener onPostExecuteListener) {
		this.onPostExecuteListener = onPostExecuteListener;
	}

	public interface OnPreExecuteListener {
		void onPreExecute();
	}

	public interface OnPostExecuteListener {
		void onPostExecute(Definition definition);
	}
}
