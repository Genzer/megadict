package com.megadict.task.base;

import android.os.AsyncTask;

public abstract class BaseSearchTask extends AsyncTask<String, Void, String>{
	@Override
	protected abstract String doInBackground(final String... params);
	@Override
	protected abstract void onPreExecute();
	@Override
	protected abstract void onPostExecute(String result);
}
