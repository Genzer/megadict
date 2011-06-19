package com.megadict.business;

import java.io.File;

import com.megadict.exception.ConfigurationFileNotFoundException;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.DictionaryNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.exception.InvalidConfigurationFileException;


public class DictionaryCenter {
	private final DictionaryClient client = new DictionaryClient();

	public void chooseDictionary(final int dictionaryID) throws DictionaryNotFoundException
	{
		client.chooseDictionary(dictionaryID);
	}

	public String lookup(final String word) throws DictionaryNotFoundException
	{
		return client.lookup(word);
	}

	public void scanDictionaries(final File externalFile) throws InvalidConfigurationFileException, ConfigurationFileNotFoundException, DictionaryNotFoundException, IndexFileNotFoundException, DataFileNotFoundException
	{
		client.scanExternalStorage(externalFile);
	}

}
