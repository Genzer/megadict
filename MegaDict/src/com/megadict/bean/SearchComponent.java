package com.megadict.bean;

import android.widget.ProgressBar;

import com.megadict.business.ResultTextMaker;
import com.megadict.widget.ResultView;

@Deprecated
public class SearchComponent {
	public final ResultView resultView;
	public final ResultTextMaker resultTextMaker;
	public final ProgressBar progressBar;

	public SearchComponent(final ResultView resultView, final ResultTextMaker resultTextMaker, final ProgressBar progressBar) {
		this.resultView = resultView;
		this.resultTextMaker = resultTextMaker;
		this.progressBar = progressBar;
	}
}
