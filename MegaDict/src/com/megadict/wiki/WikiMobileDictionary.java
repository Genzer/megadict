package com.megadict.wiki;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.megadict.exception.NotImplementedException;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class WikiMobileDictionary implements Dictionary {
	public static final String NO_DEFINITION = "There is no definition";
	private final String PREFIX = "Wikipedia ";
	private String dictionaryName;
	private final String countryCode;

	public WikiMobileDictionary(final String countryCode) {
		this.countryCode = countryCode;
		initDictionaryName();
	}

	private void initDictionaryName() {
		final String languageName =
				Wiki.getLanguageNameByCountryCode(countryCode);
		dictionaryName = PREFIX + "(" + languageName + ")";
	}

	@Override
	public Definition lookUp(final String word) {
		final String searchedWord = word.replace(" ", "_");
		final String URL_PATTERN =
				"http://%s.mobile.wikipedia.org/transcode.php?go=%s";
		final String query =
				String.format(URL_PATTERN, countryCode, searchedWord);

		Definition definition;
		try {
			final Document doc = Jsoup.connect(query).get();
			definition =
					Definition.makeDefinition(word, doc.toString(), dictionaryName);
		} catch (final IOException e) {
			definition =
					Definition.makeNonExists(word, WikiMobileDictionary.NO_DEFINITION, dictionaryName);
		}
		return definition;
	}

	@Override
	public String getName() {
		return dictionaryName;
	}

	@Override
	public List<String> recommendWord(final String word) {
		throw new NotImplementedException();
	}

	@Override
	public List<String> recommendWord(final String word, final int prefferedNumOfWord) {
		throw new NotImplementedException();
	}

}
