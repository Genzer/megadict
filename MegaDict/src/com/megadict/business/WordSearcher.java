package com.megadict.business;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.webkit.WebView;

import com.megadict.bean.SearchComponent;
import com.megadict.model.Dictionary;
import com.megadict.task.SearchTask;

public class WordSearcher {
	private String noDefinitionStr = "There is no definition.";
	private String noDictionaryStr = "There is no dictionary";
	public static final List<SearchTask> TASKS = new Vector<SearchTask>();

	public static boolean didAllTasksFinish() {
		for(final SearchTask task : TASKS) {
			if(task.isSearching()) {
				return false;
			}
		}
		return true;
	}

	public boolean lookup(final String word, final List<Dictionary> dictionaryModels, final SearchComponent searchComponent) {
		if(!TASKS.isEmpty()) {
			// If any of task hasn't finished, searching is not allowed.
			if(!didAllTasksFinish()) {
				return false;
			}
			// If all tasks finished, clear task list.
			TASKS.clear();
		}

		final WebView resultView = searchComponent.resultView;
		final ResultTextMaker resultTextMaker = searchComponent.resultTextMaker;
		// Reset resultTextMaker to make a new search.
		resultTextMaker.resetMiddleBlock();

		if(dictionaryModels.isEmpty()) {
			resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, resultTextMaker.getNoDictionaryHTML(noDictionaryStr), "text/html", "utf-8", null);
		} else {
			final String searchedWord = word.toLowerCase(Locale.ENGLISH);

			// Create and store all tasks to list.
			for(final Dictionary dictionary : dictionaryModels) {
				final SearchTask task = new SearchTask(dictionary, searchComponent);
				task.setDictionaryName(dictionary.getName());
				task.setNoDefinitionStr(noDefinitionStr);
				TASKS.add(task);
			} // End for loop.

			// Execute them.
			for(final SearchTask task : TASKS) {
				task.execute(searchedWord);
			}
		} // End if.
		return true;
	}

	public void setNoDictionaryStr(final String noDictionaryStr) {
		this.noDictionaryStr = noDictionaryStr;
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}
}
