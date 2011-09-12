package com.megadict.business.searching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import android.widget.ProgressBar;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.HistoryManager;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public final class WordSearcher implements Observer, SearchTaskManager {
	// Aggregation variables.
	private final List<Dictionary> dictionaryModels;
	private final ResultTextMaker resultTextMaker;
	private DictionaryComponent dictionaryComponent;

	// Composition variables.
	private String noDictionaryStr = "There is no dictionary";
	private String noDefinitionStr = "There is no definition.";
	private final List<SearchTask> searchTasks =
			new ArrayList<SearchTask>();
	private final HistoryManager historyManager = new HistoryManager();

	public WordSearcher(final List<Dictionary> dictionaryModels, final ResultTextMaker resultTextMaker, final DictionaryComponent dictionaryComponent) {
		this.dictionaryModels = dictionaryModels;
		this.resultTextMaker = resultTextMaker;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	public boolean didAllSearchTasksFinish() {
		for (final SearchTask task : searchTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void search(final String word) {
		// Clear old tasks.
		searchTasks.clear();

		// Reset resultTextMaker to make a new search.
		resultTextMaker.resetMiddleBlock();

		if (dictionaryModels.isEmpty()) {
			dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, resultTextMaker.getNoDictionaryHTML(noDictionaryStr), "text/html", "utf-8", null);
		} else {
			// Lower and trim it.
			final String searchedWord = word.toLowerCase(Locale.ENGLISH).trim();

			// Only search if searchedWord is not empty.
			if (!"".equals(searchedWord)) {
				createAndStoreSearchTasks();
				executeSearchTasks(searchedWord);
			}
		} // End if.
	}

	private void executeSearchTasks(final String searchedWord) {
		for (final SearchTask task : searchTasks) {
			task.execute(searchedWord);
		}
	}

	private void createAndStoreSearchTasks() {
		for (final Dictionary dictionary : dictionaryModels) {
			final SearchTask task = new SearchTask(dictionary);
			setOnPreExecuteListener(task);
			setOnPostExecuteListener(task);
			searchTasks.add(task);
		}
	}

	public void setNoDictionaryStr(final String noDictionaryStr) {
		this.noDictionaryStr = noDictionaryStr;
	}

	public void setDictionaryComponent(final DictionaryComponent dictionaryComponent) {
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	public void update(final Observable o, final Object arg) {
		@SuppressWarnings("unchecked")
		final List<Dictionary> models = (List<Dictionary>) (arg);
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}

	private void setOnPostExecuteListener(final SearchTask task) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Definition>() {
			@Override
			public void onPostExecute(final Definition definition) {
				if(dictionaryComponent == null) return;

				resultTextMaker.appendContent(definition.getWord(), definition.exists()
						? definition.getContent() : noDefinitionStr, definition.getDictionaryName());
				dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, resultTextMaker.getResultHTML(), "text/html", "utf-8", null);

				// Hide progress bar if all tasks finished.
				if (didAllSearchTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
					// Save word to history.
					saveWordToHistory(definition.getWord());
				}
			}
		});
	}

	private void setOnPreExecuteListener(final SearchTask task) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllSearchTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	// /////// ================ History operations ==================///////
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
