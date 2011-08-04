package com.megadict.bean;

import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

public class RecommendComponent {
	public final AutoCompleteTextView searchBar;
	public final WebView resultView;
	public final ProgressBar progressBar;

	public RecommendComponent(final AutoCompleteTextView searchBar, final WebView resultView, final ProgressBar progressBar) {
		this.searchBar = searchBar;
		this.resultView = resultView;
		this.progressBar = progressBar;
	}
}
