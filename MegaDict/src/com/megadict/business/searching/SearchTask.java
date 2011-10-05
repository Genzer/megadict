package com.megadict.business.searching;

import com.megadict.business.AbstractWorkerTask;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

class SearchTask extends AbstractWorkerTask<String, Void, Definition> {
	private final Dictionary dictionary;
	private OnCompleteSearchListener onCompleteSearchListener;

	public SearchTask(final Dictionary dictionary) {
		super();
		this.dictionary = dictionary;
	}

	public static SearchTask create(final Dictionary model) {
		return new SearchTask(model);
	}

	@Override
	protected Definition doInBackground(final String... words) {
		final Definition definition = dictionary.lookUp(words[0]);
		if(definition.exists() && onCompleteSearchListener != null) {
			return onCompleteSearchListener.onCompleteSearch(definition);
		}
		return definition;
	}

	/**
	 * Set onCompleteSearchListener.
	 * This listener runs on background thread, particularly used for formatting search result.
	 * @param onCompleteSearchListener
	 */
	public void setOnCompleteSearchListener(final OnCompleteSearchListener onCompleteSearchListener) {
		this.onCompleteSearchListener = onCompleteSearchListener;
	}

	public interface OnCompleteSearchListener {
		Definition onCompleteSearch(Definition definition);
	}
}
