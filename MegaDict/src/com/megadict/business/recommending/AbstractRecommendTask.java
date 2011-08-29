package com.megadict.business.recommending;

import java.util.List;

import android.os.AsyncTask;

import com.megadict.business.Workable;

public abstract class AbstractRecommendTask extends AsyncTask<String, Void, List<String>> implements Workable {
	protected OnPreExecuteListener onPreExecuteListener;
	protected OnPostExecuteListener onPostExecuteListener;
	protected boolean working;

	@Override
	protected void onPreExecute() {
		if (onPreExecuteListener != null) {
			onPreExecuteListener.onPreExecute();
		}
		working = true;
	}

	@Override
	protected abstract List<String> doInBackground(final String... params);

	@Override
	protected void onPostExecute(final List<String> list) {
		working = false;
		if (onPostExecuteListener != null) {
			onPostExecuteListener.onPostExecute(list);
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
		void onPostExecute(List<String> list);
	}
}
