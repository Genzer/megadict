package com.megadict.business.searching;

import com.megadict.business.AbstractWorkerTask;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class SearchTask extends AbstractWorkerTask<String, Void, Definition> {
	private final Dictionary dictionary;

	public SearchTask(final Dictionary dictionary) {
		super();
		this.dictionary = dictionary;
	}

	public static SearchTask create(final Dictionary model) {
		return new SearchTask(model);
	}

	@Override
	protected Definition doInBackground(final String... words) {
		return dictionary.lookUp(words[0]);
	}
}
