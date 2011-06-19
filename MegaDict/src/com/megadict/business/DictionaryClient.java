package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.Definition;

import com.megadict.bean.DictionaryBean;
import com.megadict.exception.ConfigurationFileNotFoundException;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.DictionaryNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.exception.InvalidConfigurationFileException;
import com.megadict.model.DictionaryInformation;

import format.dict.DICTDictionary;

public class DictionaryClient {
	private DICTDictionary dictionaryModel;
	private final List<DictionaryInformation> dictionaryInfos = new ArrayList<DictionaryInformation>();

	/**
	 * Choose dictionary.
	 * 
	 * @param dictionaryID
	 * @throws DictionaryNotFoundException
	 */
	public void chooseDictionary(final int dictionaryID) throws DictionaryNotFoundException {
		if(dictionaryInfos.size() == 0) throw new DictionaryNotFoundException();
		for (final DictionaryInformation info : dictionaryInfos) {
			final DictionaryBean bean = info.getBean();
			if (bean.getId() == dictionaryID) {
				final String indexFilePath = info.getIndexFile().getPath();
				final String dataFilePath = info.getDataFile().getPath();

				// BUG HERE
				//dictionaryModel = new DICTDictionary(indexFilePath, dataFilePath);
			}
		}
	}

	/**
	 * Lookup a word.
	 * 
	 * @param word
	 * @return Meaning of the word.
	 * @throws DictionaryNotFoundException if no directory found.
	 */
	public String lookup(final String word) throws DictionaryNotFoundException {
		if (dictionaryModel == null || dictionaryInfos.size() == 0)
			throw new DictionaryNotFoundException();
		//final Definition d = dictionaryModel.lookUp(word);
		final Definition d = new Definition("pers", "hieu", "hieu tot");
		return d.getContent();
	}

	/**
	 * Scan external storage for new dictionaries.
	 * 
	 * @param externalFile External storage path.
	 * @throws InvalidConfigurationFileException if configuration file invalid.
	 * @throws DictionaryNotFoundException if no dictionary found.
	 * @throws ConfigurationFileNotFoundException if configuration file not found.
	 * @throws DataFileNotFoundException if data file not found.
	 * @throws IndexFileNotFoundException if index file not found.
	 */
	public void scanExternalStorage(final File externalFile) throws InvalidConfigurationFileException, ConfigurationFileNotFoundException, DictionaryNotFoundException, IndexFileNotFoundException, DataFileNotFoundException {
		final File files[] = externalFile.listFiles();
		if (files == null) throw new InvalidConfigurationFileException();
		if (files.length == 0) throw new DictionaryNotFoundException();

		for (final File file : files) {
			if (file.isDirectory()) {
				final ConfigurationReader reader = new ConfigurationReader();
				final File configurationFile = getConfigurationFile(file);
				final DictionaryBean bean = reader.readConfigurationFile(configurationFile);
				final DictionaryInformation info = new DictionaryInformation(bean, file);
				dictionaryInfos.add(info);
			}
		}
	}

	private File getConfigurationFile(final File parentDirectory) throws ConfigurationFileNotFoundException {
		final File files[] = parentDirectory.listFiles();
		for (final File file : files) {
			if (file.getName().equals("conf.xml")) {
				return file;
			}
		}
		throw new ConfigurationFileNotFoundException();
	}

}
