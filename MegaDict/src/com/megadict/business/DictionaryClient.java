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
	private List<DictionaryInformation> dictionaryInfos = new ArrayList<DictionaryInformation>();

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

	public String lookup(final String word) throws DictionaryNotFoundException {
		if (dictionaryModel == null || dictionaryInfos.size() == 0)
			throw new DictionaryNotFoundException();
		//final Definition d = dictionaryModel.lookUp(word);
		final Definition d = new Definition("pers", "hieu", "hieu tot");
		return d.getContent();
	}

	public void scanExternalStorage(final File externalFile) throws InvalidConfigurationFileException, ConfigurationFileNotFoundException, DictionaryNotFoundException, IndexFileNotFoundException, DataFileNotFoundException {
		// Reset dictionary info array
		dictionaryInfos = ExternalReader.getDictionaryInformationList(externalFile);
	}

}
