package com.megadict.business.searching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.widget.ProgressBar;

import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.HistoryManager;
import com.megadict.business.ResultTextFormatter;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.searching.SearchTask.OnCompleteSearchListener;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public final class WordSearcher {
	private static final int DEFINITION_NOT_FOUND = 0;
	private static final int DEFINITION_FOUND = 1;

	// Aggregation variables.
	private final List<Dictionary> dictionaryModels = new ArrayList<Dictionary>();
	private DictionaryComponent dictionaryComponent;

	// Flag variables.
	private int searchResult = DEFINITION_NOT_FOUND;

	// Composition variables.
	private String noDictionaryStr = "There is no dictionary (this should not be shown).";
	private String noDefinitionStr = "There is no definition (this should not be shown).";
	private final List<SearchTask> searchTasks =
			new ArrayList<SearchTask>();
	private final HistoryManager historyManager = new HistoryManager();

	public WordSearcher(final DictionaryComponent dictionaryComponent) {
		this.dictionaryComponent = dictionaryComponent;
	}

	public boolean didAllSearchTasksFinish() {
		for (final SearchTask task : searchTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	public void search(final String word) {
		// Reset flag.
		searchResult = DEFINITION_NOT_FOUND;

		// Clear old tasks.
		searchTasks.clear();

		// Reset resultTextMaker to make a new search.
		ResultTextMaker.resetMiddleBlock();

		if (dictionaryModels.isEmpty()) {
			dictionaryComponent.getResultView().loadDataWithBaseURL(
					ResultTextMaker.ASSET_URL,
					ResultTextMaker.getNoResultHTML(word, noDictionaryStr),
					"text/html", "utf-8", null);
		} else {
			// Lower and trim it.
			final String searchedWord = word.trim().toLowerCase(Locale.US);

			// Only search if searchedWord is not empty.
			if (!"".equals(searchedWord)) {
				createAndStoreSearchTasks();
				executeSearchTasks(searchedWord);
			}
		} // End if.
	}

	public void setNoDictionaryStr(final String noDictionaryStr) {
		this.noDictionaryStr = noDictionaryStr;
	}

	public void setNoDefinitionStr(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	public void setDictionaryComponent(final DictionaryComponent dictionaryComponent) {
		this.dictionaryComponent = dictionaryComponent;
	}

	public void updateDictionaryModels(final List<Dictionary> models) {
		dictionaryModels.clear();
		dictionaryModels.addAll(models);
	}

	// /////// ================ History operations ==================///////
	private void saveWordToHistory(final String word) {
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
		return Collections.unmodifiableList(new ArrayList<String>(historyManager.getWordSet()));
	}

	//////// ================== Private functions ================== //
	private void executeSearchTasks(final String searchedWord) {
		for (final SearchTask task : searchTasks) {
			task.execute(searchedWord);
		}
	}

	private void createAndStoreSearchTasks() {
		for (final Dictionary dictionary : dictionaryModels) {
			final SearchTask task = SearchTask.create(dictionary);
			setOnCompleteSearchLisener(task, dictionary);
			setOnPreExecuteListener(task);
			setOnPostExecuteListener(task);
			searchTasks.add(task);
		}
	}

	private void setOnCompleteSearchLisener(final SearchTask task, final Dictionary model) {
		task.setOnCompleteSearchListener(new OnCompleteSearchListener() {
			@Override
			public Definition onCompleteSearch(final Definition definition) {
				// Add colors to content.
				if(model instanceof DICTDictionary) {
					final String formattedContent = ResultTextFormatter.format(definition.getContent());
					return Definition.makeDefinition(definition.getWord(), formattedContent, definition.getDictionaryName());
				}
				return definition;
			}
		});
	}

	private void setOnPostExecuteListener(final SearchTask task) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Definition>() {
			@Override
			public void onPostExecute(final Definition definition) {
				if(dictionaryComponent == null) return;

				if(definition.exists()) {
					searchResult = DEFINITION_FOUND;
					// Append each result of each dictionary to final result.
					ResultTextMaker.appendContent(definition.getWord(), definition.getContent(), definition.getDictionaryName());
				}

				// Hide progress bar if all tasks finished.
				if (didAllSearchTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);

					// If not found, report it out.
					dictionaryComponent.getResultView().loadDataWithBaseURL(
							ResultTextMaker.ASSET_URL,
							searchResult == DEFINITION_NOT_FOUND ?
									ResultTextMaker.getNoResultHTML(definition.getWord(), noDefinitionStr) :
										ResultTextMaker.getResultHTML(),
										"text/html", "utf-8", null);

					// Save the searched word to history only if it is found.
					if(searchResult == DEFINITION_FOUND)
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
}
