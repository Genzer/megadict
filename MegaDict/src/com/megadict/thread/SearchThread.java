package com.megadict.thread;

import java.util.concurrent.Callable;

import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class SearchThread implements Callable<String> {
	private final Dictionary dictionaryModel;
	private final String word;
	private final String noDefinitionStr;

	public SearchThread(final Dictionary dictionaryModel, final String word, final String noDefinitionStr) {
		this.dictionaryModel = dictionaryModel;
		this.word = word;
		this.noDefinitionStr = noDefinitionStr;
	}

	@Override
	public String call() throws Exception {
		final Definition d = dictionaryModel.lookUp(word);
		return (d == Definition.NOT_FOUND ? noDefinitionStr : d.getContent());
	}
}