package com.megadict.business;

import android.os.AsyncTask;


public abstract class AbstractWorkerTask<Params, Progress, Result>
extends AsyncTask<Params, Progress, Result>
implements Workable {
	protected OnPreExecuteListener onPreExecuteListener;
	protected OnPostExecuteListener<Result> onPostExecuteListener;
	private boolean working;

	@Override
	protected void onPreExecute() {
		if (onPreExecuteListener != null) {
			onPreExecuteListener.onPreExecute();
		}
		working = true;
	}

	@Override
	protected void onPostExecute(final Result result) {
		working = false;
		if (onPostExecuteListener != null) {
			onPostExecuteListener.onPostExecute(result);
		}
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	public void setOnPreExecuteListener(final OnPreExecuteListener onPreExecuteListener) {
		this.onPreExecuteListener = onPreExecuteListener;
	}

	public void setOnPostExecuteListener(final OnPostExecuteListener<Result> onPostExecuteListener) {
		this.onPostExecuteListener = onPostExecuteListener;
	}

	public interface OnPreExecuteListener {
		void onPreExecute();
	}

	public interface OnPostExecuteListener<Result> {
		void onPostExecute(Result result);
	}
}