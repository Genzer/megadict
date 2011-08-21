package com.megadict.thread;

import java.util.concurrent.Callable;

import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Dictionary;

public class CreateDICTThread implements Callable<Dictionary> {
	private final IndexFile indexFile;
	private final DictionaryFile dictionaryFile;

	public CreateDICTThread(final IndexFile indexFile, final DictionaryFile dictionaryFile) {
		this.indexFile = indexFile;
		this.dictionaryFile = dictionaryFile;
	}

	@Override
	public Dictionary call() throws Exception {
		return new DICTDictionary(indexFile, dictionaryFile);
	}
}