package com.megadict.business.searching;

import android.widget.ProgressBar;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.Definition;

public class SearchTaskInitializer {
	private String noDefinitionStr = "There is no definition.";
	private final WordSearcher searcher;
	private final DictionaryComponent dictionaryComponent;

	public SearchTaskInitializer(final WordSearcher searcher, final DictionaryComponent dictionaryComponent) {
		this.searcher = searcher;
		this.dictionaryComponent = dictionaryComponent;
	}

	public void setOnPostExecuteListener(final SearchTask task) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Definition>() {
			@Override
			public void onPostExecute(final Definition definition) {
				dictionaryComponent.getResultTextMaker().appendContent(definition.getWord(), definition.exists()
						? definition.getContent() : noDefinitionStr, definition.getDictionaryName());
				dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, dictionaryComponent.getResultTextMaker().getResultHTML(), "text/html", "utf-8", null);

				// Hide progress bar if all tasks finished.
				if (searcher.didAllSearchTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
					// Save word to history.
					searcher.saveWordToHistory(definition.getWord());
				}
			}
		});
	}

	public void setOnPreExecuteListener(final SearchTask task) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (searcher.didAllSearchTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}
}
