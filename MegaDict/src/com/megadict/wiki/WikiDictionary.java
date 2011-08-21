package com.megadict.wiki;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.megadict.exception.NotImplementedException;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class WikiDictionary implements Dictionary {
	private final int ALLOWED_WORD_COUNT = 1000;
	private final int ALLOWED_PARAGRAPH_COUNT = 2;
	private final String PREFIX = "Wikipedia ";
	private String dictionaryName;
	private final String countryCode;

	public WikiDictionary(final String countryCode) {
		this.countryCode = countryCode;
		initDictionaryName();
	}

	private void initDictionaryName() {
		final String languageName = Wiki.getLanguageNameByCountryCode(countryCode);
		dictionaryName = PREFIX + "(" + languageName + ")";
	}

	@Override
	public Definition lookUp(final String word) {
		final String query = "http://" + countryCode + ".wikipedia.org/w/index.php?title=" + word + "&action=render";
		Definition definition;
		try {
			final StringBuilder builder = new StringBuilder();
			final Document doc = Jsoup.connect(query).get();
			final Elements pTags = doc.select("p");
			if(pTags.size() < ALLOWED_PARAGRAPH_COUNT) {
				definition = Definition.NOT_FOUND;
			} else {
				for(int i = 0, wordCount = 0; i < pTags.size(); ++i) {
					final String temp = pTags.get(i).text();
					wordCount += temp.length();

					if(wordCount > ALLOWED_WORD_COUNT)
						break;

					builder.append(pTags.get(i));
				}
				final String result =  Jsoup.parseBodyFragment(builder.toString()).toString();
				definition = new Definition(word, result, "dictionarry name");
			}
		} catch(final Exception e) {
			definition = Definition.NOT_FOUND;
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
