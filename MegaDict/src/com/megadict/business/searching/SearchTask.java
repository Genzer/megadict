package com.megadict.business.searching;

import com.megadict.business.AbstractWorkerTask;
import com.megadict.business.ResultTextFormatter;
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
		final Definition d = dictionary.lookUp(words[0]);
		if(!d.exists()) return d;

		// Add colors to content.
		final String formattedContent = ResultTextFormatter.format(d.getContent());
		return Definition.makeDefinition(d.getWord(), formattedContent, d.getDictionaryName());
	}
}
