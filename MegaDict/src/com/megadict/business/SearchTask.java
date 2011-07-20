package com.megadict.business;

import java.util.List;

import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

// ============================ Search task inner class =============== //
public class SearchTask extends AsyncTask<String, Void, List<String>> {
	private final ProgressBar progressBar;
	private boolean searching = false;
	private final DictionaryClient dictionaryClient;
	private final ResultTextMaker resultTextMaker;
	private final String noDictionaryString;
	private final WebView resultView;

	public SearchTask(final DictionaryClient dictionaryClient, final ResultTextMaker resultTextMaker, final ProgressBar progressBar, final WebView resultView, final String noDictionaryString) {
		super();
		this.dictionaryClient = dictionaryClient;
		this.resultTextMaker = resultTextMaker;
		this.progressBar = progressBar;
		this.noDictionaryString = noDictionaryString;
		this.resultView = resultView;
	}

	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
		searching = true;
	}

	@Override
	protected List<String> doInBackground(final String... params) {
		return search(params[0]);
	}

	@Override
	protected void onPostExecute(final List<String> list) {
		updateUI(list);
		progressBar.setVisibility(View.INVISIBLE);
		searching = false;
	}

	private List<String> search(final String word) {
		return dictionaryClient.lookup(word);
	}

	private void updateUI(final List<String> contents) {
		upateResultView(contents, dictionaryClient.getDictionaryNames());
	}

	private void upateResultView(final List<String> contents, final List<String> dictionaryNames) {
		String resultText;
		if(contents.isEmpty()) {
			resultText = resultTextMaker.getNoDictionaryHTML(noDictionaryString);
		} else {
			resultText = resultTextMaker.getResultHTML(contents, dictionaryNames);
		}
		resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, resultText, "text/html", "utf-8", null);
	}

	public boolean isSearching() {
		return searching;
	}
}