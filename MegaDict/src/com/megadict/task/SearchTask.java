package com.megadict.task;

import android.view.View;

import com.megadict.bean.SearchComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.WordSearcher;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;
import com.megadict.task.base.BaseSearchTask;

public class SearchTask extends BaseSearchTask {
	private final Dictionary dictionary;
	private final SearchComponent searchComponent;
	private String dictionaryName = "<Dictionary name>";
	private String noDefinitionStr = "There is no definition.";
	private boolean searching;

	public SearchTask(final Dictionary dictionary, final SearchComponent searchComponent) {
		this.dictionary = dictionary;
		this.searchComponent = searchComponent;
	}

	@Override
	protected void onPreExecute() {
		searchComponent.progressBar.setVisibility(View.VISIBLE);
		searching = true;
	}

	@Override
	protected String doInBackground(final String... words) {
		final Definition d = dictionary.lookUp(words[0]);
		String content;
		if(d == Definition.NOT_FOUND) {
			content = noDefinitionStr;
		} else {
			content = d.getContent();
		}
		return content;
	}

	@Override
	protected void onPostExecute(final String content) {
		searching = false;
		searchComponent.resultTextMaker.appendContent(content, dictionaryName);
		searchComponent.resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, searchComponent.resultTextMaker.getResultHTML(), "text/html", "utf-8", null);

		// Hide progress bar if all tasks finished.
		if(WordSearcher.didAllTasksFinish()) {
			searchComponent.progressBar.setVisibility(View.INVISIBLE);
		}
	}

	public void setDictionaryName(final String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	public boolean isSearching() {
		return searching;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}
}
