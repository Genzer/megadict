package com.megadict.adapter;

import com.megadict.bean.DictionaryBean;

public class DictionaryListItem {

	private DictionaryBean dictionaryBean;
	private boolean dictionaryEnabled;

	public DictionaryListItem(final DictionaryBean dictionaryBean) {
		this.dictionaryBean = dictionaryBean;
		dictionaryEnabled = false;
	}

	public DictionaryBean getDictionaryBean() {
		return dictionaryBean;
	}

	public void setDictionaryBean(final DictionaryBean dictionaryBean) {
		this.dictionaryBean = dictionaryBean;
	}

	public boolean isDictionaryEnabled() {
		return dictionaryEnabled;
	}

	public void setDictionaryEnabled(final boolean dictionaryEnabled) {
		this.dictionaryEnabled = dictionaryEnabled;
	}

}