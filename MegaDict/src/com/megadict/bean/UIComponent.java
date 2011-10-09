package com.megadict.bean;

import java.util.List;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.megadict.widget.ResultView;

public class UIComponent {
	private final Button searchButton;
	private final Button pronounceButton;
	private final AutoCompleteTextView searchBar;
	private final ResultView resultView;
	private final ProgressBar progressBar;
	private final List<Button> bottomButtons;

	public static class Builder {
		private Button searchButton;
		private Button pronounceButton;
		private AutoCompleteTextView searchBar;
		private ResultView resultView;
		private ProgressBar progressBar;
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

		public Builder progressBar(final ProgressBar progressBar) {
			this.progressBar = progressBar;
			return this;
		}

		public Builder bottomButtons(final List<Button> bottomButtons) {
			this.bottomButtons = bottomButtons;
			return this;
		}

		public UIComponent build() {
			return new UIComponent(this);
		}
	}

	public UIComponent(final Builder builder) {
		this.searchButton = builder.searchButton;
		this.pronounceButton = builder.pronounceButton;
		this.searchBar = builder.searchBar;
		this.resultView = builder.resultView;
		this.progressBar = builder.progressBar;
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
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public List<Button> getBottomButtons() {
		return bottomButtons;
	}

}
