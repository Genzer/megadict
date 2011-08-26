package com.megadict.bean;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.megadict.business.ResultTextMaker;
import com.megadict.widget.ResultView;

public class DictionaryComponent {
	private final Button searchButton;
	private final AutoCompleteTextView searchBar;
	private final ResultView resultView;
	private final ResultTextMaker resultTextMaker;
	private final ProgressBar progressBar;
	private final Context context;

	public static class Builder {
		private Button searchButton;
		private AutoCompleteTextView searchBar;
		private ResultView resultView;
		private ResultTextMaker resultTextMaker;
		private ProgressBar progressBar;
		private Context context;

		public Builder searchButton(final Button searchButton) {
			this.searchButton = searchButton; return this;
		}

		public Builder searchBar(final AutoCompleteTextView searchBar) {
			this.searchBar = searchBar;	return this;
		}

		public Builder resultView(final ResultView resultView) {
			this.resultView = resultView; return this;
		}

		public Builder resultTextMaker(final ResultTextMaker resultTextMaker) {
			this.resultTextMaker = resultTextMaker; return this;
		}

		public Builder progressBar(final ProgressBar progressBar) {
			this.progressBar = progressBar; return this;
		}

		public Builder context(final Context context) {
			this.context = context; return this;
		}

		public DictionaryComponent build() {
			return new DictionaryComponent(this);
		}
	}

	public DictionaryComponent(final Builder builder) {
		this.searchButton = builder.searchButton;
		this.searchBar = builder.searchBar;
		this.resultView = builder.resultView;
		this.resultTextMaker = builder.resultTextMaker;
		this.progressBar = builder.progressBar;
		this.context = builder.context;
	}

	public Button getSearchButton() {
		return searchButton;
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

}
