package com.megadict.business.searching;

import android.view.View;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class SearchTask extends AbstractSearchTask {
	private final WordSearcher searcher;
	private final Dictionary dictionary;
	private final DictionaryComponent dictionaryComponent;
	private String dictionaryName = "<Dictionary name>";
	private String noDefinitionStr = "There is no definition.";

	// Member variables.
	private String word;

	public SearchTask(final WordSearcher searcher, final Dictionary dictionary, final DictionaryComponent dictionaryComponent) {
		super();
		this.searcher = searcher;
		this.dictionary = dictionary;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected void onPreExecute() {
		if(searcher.didAllSearchTasksFinish()) {
			dictionaryComponent.getProgressBar().setVisibility(View.VISIBLE);
		}
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(final String... words) {
		word = words[0];

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
		super.onPostExecute(content);
		dictionaryComponent.getResultTextMaker().appendContent(word, content, dictionaryName);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, dictionaryComponent.getResultTextMaker().getResultHTML(), "text/html", "utf-8", null);

		// Hide progress bar if all tasks finished.
		if(searcher.didAllSearchTasksFinish()) {
			dictionaryComponent.getProgressBar().setVisibility(View.INVISIBLE);
			// Save word to history.
			searcher.saveWordToHistory(word);
		}
	}

	public void setDictionaryName(final String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}
}
