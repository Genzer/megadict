package com.megadict.business.searching;

import android.view.View;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class SearchTask extends BaseSearchTask {
	private final Dictionary dictionary;
	private final DictionaryComponent dictionaryComponent;
	private String dictionaryName = "<Dictionary name>";
	private String noDefinitionStr = "There is no definition.";

	public SearchTask(final Dictionary dictionary, final DictionaryComponent dictionaryComponent) {
		super();
		this.dictionary = dictionary;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dictionaryComponent.getProgressBar().setVisibility(View.VISIBLE);
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
		super.onPostExecute(content);
		dictionaryComponent.getResultTextMaker().appendContent(content, dictionaryName);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, dictionaryComponent.getResultTextMaker().getResultHTML(), "text/html", "utf-8", null);

		// Hide progress bar if all tasks finished.
		if(WordSearcher.didAllTasksFinish()) {
			dictionaryComponent.getProgressBar().setVisibility(View.INVISIBLE);
		}
	}

	public void setDictionaryName(final String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}
}
