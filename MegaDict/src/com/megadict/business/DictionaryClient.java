package com.megadict.business;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public class DictionaryClient {
	// Aggregation variables.
	private final BusinessComponent businessComponent;
	private final DictionaryComponent dictionaryComponent;
	private final Context context;

	public Context getContext() {
		return context;
	}

	public ResultView getResultView() {
		return dictionaryComponent.getResultView();
	}

	public AutoCompleteTextView getSearchBar() {
		return dictionaryComponent.getSearchBar();
	}

	public Button getSearchButton() {
		return dictionaryComponent.getSearchButton();
	}

	public List<Button> getBottomButtons() {
		return dictionaryComponent.getBottomButtons();
	}

	public void removeWordFromHistory(final String word) {
		businessComponent.getSearcher().removeWordFromHistory(word);
	}

	public void removeWordFromHistory(final Collection<String> words) {
		businessComponent.getSearcher().removeWordFromHistory(words);
	}

	public void clearHistory() {
		businessComponent.getSearcher().clearHistory();
	}

	public List<String> getHistoryList() {
		return businessComponent.getSearcher().getHistoryList();
	}

	public DictionaryClient(final Context context, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super();
		this.context = context;
		this.businessComponent = businessComponent;
		this.dictionaryComponent = dictionaryComponent;
	}

	/**
	 * Recommend a word.
	 */
	public void recommend() {
		businessComponent.getRecommender().recommend(getWordOnSearchBar());
	}

	/**
	 * This function does two jobs: Prevent recommending and search.
	 * @param word
	 */
	public void search() {
		preventRecommending();
		searchHelper(getWordOnSearchBar());
	}

	/**
	 * This function does three jobs: Insert word to search bar, prevent recommending and search.
	 * @param word
	 */
	public void searchWithUI() {
		final EditText searchBar = dictionaryComponent.getSearchBar();
		searchBar.setText(getWordOnSearchBar());
		preventRecommending();
		searchHelper(getWordOnSearchBar());
	}

	/**
	 * This function does three jobs: Insert word to search bar, prevent recommending and search.
	 * @param word
	 */
	public void searchWithUI(final String word) {
		final EditText searchBar = dictionaryComponent.getSearchBar();
		searchBar.setText(word);
		preventRecommending();
		searchHelper(word);
	}

	private void searchHelper(final String word) {
		// Get useful components.
		final WordSearcher searcher = businessComponent.getSearcher();
		final WordRecommender recommender = businessComponent.getRecommender();

		// Cancel recommending anyway if searching triggers!
		recommender.cancelRecommending();

		if (searcher.didAllSearchTasksFinish()) {
			searcher.search(word);
		} else {
			Utility.messageBox(context, R.string.searching);
		}
	}

	private void preventRecommending() {
		businessComponent.getRecommender().preventRecommending();
	}

	private String getWordOnSearchBar() {
		return dictionaryComponent.getSearchBar().getText().toString();
	}
}
