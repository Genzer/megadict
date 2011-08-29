package com.megadict.bean;

import android.content.Context;

import com.megadict.business.ResultTextMaker;
import com.megadict.widget.ResultView;

@Deprecated
public class ScanStorageComponent {
	public ResultView resultView;
	public ResultTextMaker resultTextMaker;
	public Context context;

	public ScanStorageComponent(final ResultView resultView, final ResultTextMaker resultTextMaker, final Context context) {
		this.resultView = resultView;
		this.resultTextMaker = resultTextMaker;
		this.context = context;
	}
}
