package com.megadict.task;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.megadict.business.DictionaryClient;

public class RecommendTask extends AsyncTask<String, Void, List<String>> {
	private final DictionaryClient dictionaryClient;
	private final ProgressBar progressBar;
	private final AutoCompleteTextView searchEditText;
	private boolean recommending;
	private final Context context;

	public RecommendTask(final Context context, final DictionaryClient dictionaryClient, final ProgressBar progressBar, final AutoCompleteTextView searchEditText) {
		this.context = context;
		this.dictionaryClient = dictionaryClient;
		this.progressBar = progressBar;
		this.searchEditText = searchEditText;
	}

	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
		recommending = true;
	}

	@Override
	protected List<String> doInBackground(final String... params) {
		return dictionaryClient.recommend(params[0]);
	}

	@Override
	protected void onPostExecute(final List<String> list) {
		updateUI(list);
		progressBar.setVisibility(View.INVISIBLE);
		recommending = false;
	}

	private void updateUI(final List<String> recommendWords) {
		searchEditText.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, recommendWords));
	}

	public boolean isRecommeding() {
		return recommending;
	}
}
