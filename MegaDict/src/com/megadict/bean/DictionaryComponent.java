package com.megadict.bean;

import java.util.List;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.megadict.business.ResultTextMaker;
import com.megadict.widget.ResultView;

public class DictionaryComponent {
	private final Button searchButton;
	private final Button pronounceButton;
	private final AutoCompleteTextView searchBar;
	private final ResultView resultView;
	private final ResultTextMaker resultTextMaker;
	private final ProgressBar progressBar;
	private final Context context;
	private final List<Button> bottomButtons;

	public static class Builder {
		private Button searchButton;
		private Button pronounceButton;
		private AutoCompleteTextView searchBar;
		private ResultView resultView;
		private ResultTextMaker resultTextMaker;
		private ProgressBar progressBar;
		private Context context;
		private List<Button> bottomButtons;

		public Builder searchButton(final Button searchButton) {
			this.searchButton = searchButton;
			return this;
		}

		public Builder pronounceButton(final Button pronounceButton) {
			this.pronounceButton = pronounceButton;
			return this;
		}

		public Builder searchBar(final AutoCompleteTextView searchBar) {
			this.searchBar = searchBar;
			return this;
		}

		public Builder resultView(final ResultView resultView) {
			this.resultView = resultView;
			return this;
		}

		public Builder resultTextMaker(final ResultTextMaker resultTextMaker) {
			this.resultTextMaker = resultTextMaker;
			return this;
		}

		public Builder progressBar(final ProgressBar progressBar) {
			this.progressBar = progressBar;
			return this;
		}

		public Builder context(final Context context) {
			this.context = context;
			return this;
		}

		public Builder bottomButtons(final List<Button> bottomButtons) {
			this.bottomButtons = bottomButtons;
			return this;
		}

		public DictionaryComponent build() {
			return new DictionaryComponent(this);
		}
	}

	public DictionaryComponent(final Builder builder) {
		this.searchButton = builder.searchButton;
		this.pronounceButton = builder.pronounceButton;
		this.searchBar = builder.searchBar;
		this.resultView = builder.resultView;
		this.resultTextMaker = builder.resultTextMaker;
		this.progressBar = builder.progressBar;
		this.context = builder.context;
		this.bottomButtons = builder.bottomButtons;
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public Button getPronounceButton() {
		return pronounceButton;
	}

	public AutoCompleteTextView getSearchBar() {
		return searchBar;
	}

	public ResultView getResultView() {
		return resultView;
	}

	public ResultTextMaker getResultTextMaker() {
		return resultTextMaker;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public Context getContext() {
		return context;
	}

	public List<Button> getBottomButtons() {
		return bottomButtons;
	}

}
