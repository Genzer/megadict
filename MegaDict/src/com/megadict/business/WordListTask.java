package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

public class WordListTask extends AbstractWorkerTask<String, Void, List<String>> {
	private final static String SPLIT_REGEX = "[^\\w]+";

	@Override
	protected List<String> doInBackground(final String... params) {
		final String []items = params[0].split(SPLIT_REGEX);
		final List<String> words = new ArrayList<String>();
		for (final String item : items) {
			if (!"".equals(item)) {
				words.add(item);
			}
		}
		return words;
	}
}
