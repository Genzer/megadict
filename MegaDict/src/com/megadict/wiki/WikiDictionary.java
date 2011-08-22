package com.megadict.wiki;

import java.io.File;
import java.io.PrintWriter;
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
		final String searchedWord = word.replace(" ", "_");
		final String query = "http://" + countryCode + ".wikipedia.org/w/index.php?title=" + searchedWord + "&action=render";
		System.out.println(query);
		Definition definition;
		try {
			final StringBuffer builder = new StringBuffer();
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

				builder.append("<a href=\"http://en.wikipedia.org/wiki/" + searchedWord + "\">Full Article..</a>");
				final String result = builder.toString();
				final PrintWriter writer = new PrintWriter(new File("/sdcard/Download/megadict/debug.html"));
				writer.println(builder.toString());
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
