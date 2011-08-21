package com.megadict.business.scanning;

import java.util.concurrent.Callable;

import com.megadict.model.Dictionary;
import com.megadict.wiki.WikiDictionary;

public class CreateWikiThread implements Callable<Dictionary> {
	private final String countryCode;

	public CreateWikiThread(final String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public Dictionary call() throws Exception {
		return new WikiDictionary(countryCode);
	}
}