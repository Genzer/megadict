package com.megadict.business.searching;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.Dictionary;

public final class WordSearcher implements Observer {
	private final List<Dictionary> dictionaryModels;
	private final DictionaryComponent dictionaryComponent;

	private String noDefinitionStr = "There is no definition.";
	private String noDictionaryStr = "There is no dictionary";
	public static final List<SearchTask> TASKS = new Vector<SearchTask>();

	public WordSearcher(final List<Dictionary> dictionaryModels, final DictionaryComponent dictionaryComponent) {
		this.dictionaryModels = dictionaryModels;
		this.dictionaryComponent = dictionaryComponent;
	}

	public static boolean didAllTasksFinish() {
		for(final SearchTask task : TASKS) {
			if(task.isSearching()) {
				return false;
			}
		}
		return true;
	}

	public boolean lookup(final String word) {
		if(!TASKS.isEmpty()) {
			// If any of task hasn't finished, searching is not allowed.
			if(!didAllTasksFinish()) {
				return false;
			}
			// If all tasks finished, clear task list.
			TASKS.clear();
		}

		final ResultTextMaker resultTextMaker = dictionaryComponent.getResultTextMaker();
		// Reset resultTextMaker to make a new search.
		resultTextMaker.resetMiddleBlock();

		if(dictionaryModels.isEmpty()) {
			dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, resultTextMaker.getNoDictionaryHTML(noDictionaryStr), "text/html", "utf-8", null);
		} else {
			// Lower and trim it.
			final String searchedWord = word.toLowerCase(Locale.ENGLISH).trim();

			// Only search if searchedWord is not empty.
			if(!"".equals(searchedWord)) {
				createAndStoreSearchTasks();
				executeSearchTasks(searchedWord);
			}
		} // End if.
		return true;
	}

	private void executeSearchTasks(final String searchedWord) {
		for(final SearchTask task : TASKS) {
			task.execute(searchedWord);
		}
	}

	private void createAndStoreSearchTasks() {
		for(final Dictionary dictionary : dictionaryModels) {
			final SearchTask task = new SearchTask(dictionary, dictionaryComponent);
			task.setDictionaryName(dictionary.getName());
			task.setNoDefinitionStr(noDefinitionStr);
			TASKS.add(task);
		}
	}

	public void setNoDictionaryStr(final String noDictionaryStr) {
		this.noDictionaryStr = noDictionaryStr;
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	@Override
	public void update(final Observable o, final Object arg) {
		@SuppressWarnings("unchecked")
		final List<Dictionary> models = (List<Dictionary>)(arg);
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}
}
