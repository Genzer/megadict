package com.megadict.business.searching;

import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class SearchTask extends AbstractSearchTask {
	private final Dictionary dictionary;

	public SearchTask(final Dictionary dictionary) {
		super();
		this.dictionary = dictionary;
	}

	@Override
	protected Definition doInBackground(final String... words) {
		return dictionary.lookUp(words[0]);
	}
}
