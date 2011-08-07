package com.megadict.model;

import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;

public class UsedDictionary extends DICTDictionary {
	public UsedDictionary(final IndexFile indexFile, final DictionaryFile dictFile) {
		super(indexFile, dictFile);
	}

	public static UsedDictionary newInstance(final IndexFile indexFile, final DictionaryFile dictFile) {
		return new UsedDictionary(indexFile, dictFile);
	}
}
