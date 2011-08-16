package com.megadict.business.searching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.HistoryManager;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.Dictionary;

public final class WordSearcher implements Observer {
	// Aggregation variables.
	private final List<Dictionary> dictionaryModels;
	private final DictionaryComponent dictionaryComponent;

	// Member variables.
	private String noDefinitionStr = "There is no definition.";
	private String noDictionaryStr = "There is no dictionary";
	private final List<SearchTask> taskList = new ArrayList<SearchTask>();
	private final HistoryManager historyManager = new HistoryManager();

	public WordSearcher(final List<Dictionary> dictionaryModels, final DictionaryComponent dictionaryComponent) {
		this.dictionaryModels = dictionaryModels;
		this.dictionaryComponent = dictionaryComponent;
	}

	protected boolean didAllTasksFinish() {
		for(final SearchTask task : taskList) {
			if(task.isSearching()) {
				return false;
			}
		}
		return true;
	}

	public boolean lookup(final String word) {
		if(!taskList.isEmpty()) {
			// If any of task hasn't finished, searching is not allowed.
			if(!didAllTasksFinish()) {
				return false;
			}
			// If all tasks finished, clear task list.
			taskList.clear();
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
		for(final SearchTask task : taskList) {
			task.execute(searchedWord);
		}
	}

	private void createAndStoreSearchTasks() {
		for(final Dictionary dictionary : dictionaryModels) {
			final SearchTask task = new SearchTask(this, dictionary, dictionaryComponent);
			task.setDictionaryName(dictionary.getName());
			task.setNoDefinitionStr(noDefinitionStr);
			taskList.add(task);
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



	///////// ================ History operations ==================///////
	protected void saveWordToHistory(final String word) {
		historyManager.save(word);
	}

	public void clearHistory() {
		historyManager.clear();
	}

	public void removeWordFromHistory(final Collection<String> collection) {
		historyManager.removeAll(collection);
	}

	public void removeWordFromHistory(final String word) {
		historyManager.remove(word);
	}

	public List<String> getHistoryList() {
		return new ArrayList<String>(historyManager.getWordSet());
	}
}
