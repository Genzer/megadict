package com.megadict.business;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.UIComponent;
import com.megadict.business.recommending.WordRecommender;
import com.megadict.business.searching.WordSearcher;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public class DictionaryClient {
	// Aggregation variables.
	private final BusinessComponent businessComponent;
	private final UIComponent uiComponent;
	private final Context context;

	// Member variables.
	private ArrayAdapter<String> adapter;

	public DictionaryClient(final Context context, final BusinessComponent businessComponent, final UIComponent uiComponent) {
		super();
		this.context = context;
		this.businessComponent = businessComponent;
		this.uiComponent = uiComponent;
	}

	public Context getContext() {
		return context;
	}

	public ResultView getResultView() {
		return uiComponent.getResultView();
	}

	public AutoCompleteTextView getSearchBar() {
		return uiComponent.getSearchBar();
	}

	public Button getSearchButton() {
		return uiComponent.getSearchButton();
	}

	public List<Button> getBottomButtons() {
		return uiComponent.getBottomButtons();
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

	/**
	 * Recommend a word.
	 */
	public void recommend() {
		businessComponent.getRecommender().recommend(getWordOnSearchBar());
	}

	/**
	 * This function does two jobs: Prevent recommending and search the word on search bar.
	 * @param word
	 */
	public void search() {
		uiComponent.getSearchBar().dismissDropDown();
		preventRecommending();
		searchHelper(getWordOnSearchBar());
	}

	/**
	 * This function does three jobs: Insert word to search bar, prevent recommending and search.
	 * @param word
	 */
	public void searchWithUI(final String word) {
		turnOffSuggestion();

		// Clear text then append to move the caret to the the end.
		uiComponent.getSearchBar().setText("");
		uiComponent.getSearchBar().append(word);

		turnOnSuggestion();

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

	private void turnOnSuggestion() {
		uiComponent.getSearchBar().setAdapter(adapter);
	}

	@SuppressWarnings("unchecked")
	private void turnOffSuggestion() {
		uiComponent.getSearchBar().dismissDropDown();
		adapter = (ArrayAdapter<String>)uiComponent.getSearchBar().getAdapter();
		uiComponent.getSearchBar().setAdapter((ArrayAdapter<String>)null);
	}

	private String getWordOnSearchBar() {
		return uiComponent.getSearchBar().getText().toString();
	}

	private void preventRecommending() {
		businessComponent.getRecommender().preventRecommending();
	}
}
