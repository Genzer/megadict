package com.megadict.wiki;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.megadict.exception.NotImplementedException;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class WikiDictionary implements Dictionary {
	public static final String NO_DEFINITION = "There is no definition";
	private final int ALLOWED_WORD_COUNT = 1000;
	private final int TIMEOUT = 5000;
	private final String PREFIX = "Wikipedia ";
	private String dictionaryName;
	private final String countryCode;

	public WikiDictionary(final String countryCode) {
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
		final String URI_PATTERN =
				"http://%s.wikipedia.org/w/index.php?title=%s&action=render";
		final String query =
				String.format(URI_PATTERN, countryCode, searchedWord);

		Definition definition;
		try {
			final Document doc = Jsoup.connect(query).timeout(TIMEOUT).get();
			final Elements pTags = doc.select("p");

			if (pTags.size() < 1) {
				definition =
						Definition.makeNonExists(word, NO_DEFINITION, dictionaryName);
			} else {
				// Append paragraph by word count, if it exceeds allowed word count, stop appending.
				final StringBuilder paragraphs = new StringBuilder();
				for (int i = 0; i < pTags.size(); ++i) {
					if (paragraphs.length() > ALLOWED_WORD_COUNT) break;
					paragraphs.append(pTags.get(i).toString());
				}
				// If paragraphs still empty, append first p tag to it.
				if (paragraphs.length() == 0) {
					paragraphs.append(pTags.get(0).toString());
				}
				final String FULL_ARTICLE_PATTERN =
						"<a href=\"http://en.wikipedia.org/wiki/%s\">Full Article..</a>";
				paragraphs.append(String.format(FULL_ARTICLE_PATTERN, searchedWord));
				definition =
						Definition.makeDefinition(word, paragraphs.toString(), dictionaryName);
			}
		} catch (final Exception e) {
			definition =
					Definition.makeNonExists(word, NO_DEFINITION, dictionaryName);
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
