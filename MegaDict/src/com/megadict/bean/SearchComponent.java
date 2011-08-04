package com.megadict.bean;

import android.webkit.WebView;
import android.widget.ProgressBar;

import com.megadict.business.ResultTextMaker;

public class SearchComponent {
	public final WebView resultView;
	public final ResultTextMaker resultTextMaker;
	public final ProgressBar progressBar;

	public SearchComponent(final WebView resultView, final ResultTextMaker resultTextMaker, final ProgressBar progressBar) {
		this.resultView = resultView;
		this.resultTextMaker = resultTextMaker;
		this.progressBar = progressBar;
	}
}
