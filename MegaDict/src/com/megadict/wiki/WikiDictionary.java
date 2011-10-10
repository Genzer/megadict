package com.megadict.wiki;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.util.Log;

import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class WikiDictionary implements Dictionary {
	private static final String TAG = "WikiMobileDictionary";
	private static final String NO_DEFINITION = "No definition";
	private static final String TIME_OUT = "Timeout";
	private static final int ALLOWED_WORD_COUNT = 1000;
	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String PREFIX = "Wikipedia ";
	private static final String URL_PATTERN = "http://%s.m.wikipedia.org/wiki/%s";
	private static final String FULL_ARTICLE_PATTERN =
			"<div style=\"text-align: right\"><a href=\"http://%s.wikipedia.org/wiki/%s\">Full article...</a></div>";
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
		final String query =
				String.format(URL_PATTERN, countryCode, searchedWord);
		try {
			final Document doc = Jsoup.connect(query).timeout(TIMEOUT_MILLISECONDS).get();
			final Elements selectedTags = doc.select("p, ul");

			if (selectedTags.size() < 1) {
				return Definition.makeNonExists(word, NO_DEFINITION, dictionaryName);
			} else {
				// Append paragraph by word count, if it exceeds allowed word count, stop appending.
				final StringBuilder paragraphs = new StringBuilder();
				for (int i = 0; i < selectedTags.size(); ++i) {
					if (paragraphs.length() > ALLOWED_WORD_COUNT) break;
					paragraphs.append(selectedTags.get(i).toString());
				}

				// If paragraphs still empty, append first p tag to it.
				if (paragraphs.length() == 0) {
					paragraphs.append(selectedTags.get(0).toString());
				}

				paragraphs.append(String.format(FULL_ARTICLE_PATTERN, countryCode, searchedWord));
				return Definition.makeDefinition(word, paragraphs.toString(), dictionaryName);
			}
		} catch(final SocketTimeoutException e) {
			return Definition.makeNonExists(word, TIME_OUT, dictionaryName);
		} catch (final IOException e) {
			return Definition.makeNonExists(word, NO_DEFINITION, dictionaryName);
		} catch(final Exception e) {
			// Catch this temporarily for development.
			Log.d(TAG, e.getMessage());
			return Definition.makeNonExists(word, NO_DEFINITION, dictionaryName);
		}
	}

	@Override
	public String getName() {
		return dictionaryName;
	}

	@Override
	public List<String> recommendWord(final String word) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> recommendWord(final String word, final int prefferedNumOfWord) {
		throw new UnsupportedOperationException();
	}

}