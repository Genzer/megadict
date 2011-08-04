package com.megadict.task.base;

import java.util.List;

import android.os.AsyncTask;

public abstract class BaseRecommendTask extends AsyncTask<String, Void, List<String>> {
	@Override
	protected abstract List<String> doInBackground(final String... params);
	@Override
	protected abstract void onPreExecute();
	@Override
	protected abstract void onPostExecute(List<String> result);
}
