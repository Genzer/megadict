package com.megadict.model;

import java.io.File;

import com.megadict.bean.DictionaryBean;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;

public class DictionaryInformation {
	private File indexFile;
	private File dataFile;
	private final DictionaryBean bean;

	public DictionaryInformation(final DictionaryBean bean, final File parentFilePath) throws IndexFileNotFoundException, DataFileNotFoundException {
		this.bean = bean;
		createNecessaryFiles(parentFilePath);
	}

	private void createNecessaryFiles(final File parentFilePath) throws IndexFileNotFoundException, DataFileNotFoundException {

		/* Create index and data file path. */
		final String fileNameWithoutExtension = bean.getSourceLanguage() + "_"
		+ bean.getTargetLanguage();
		final File filePath = parentFilePath;
		final String separator = System.getProperty("file.separator");
		final String indexFilePath = filePath + separator + fileNameWithoutExtension + ".index";
		final String dataFilePath = filePath + separator + fileNameWithoutExtension + ".dict";

		/* Check if index file and data file exist. If not, throw exception. */
		indexFile = new File(indexFilePath);
		dataFile = new File(dataFilePath);
		if (!indexFile.exists()) throw new IndexFileNotFoundException();
		if (!dataFile.exists()) throw new DataFileNotFoundException();
	}

	public File getIndexFile() {
		return indexFile;
	}

	public File getDataFile() {
		return dataFile;
	}

	public DictionaryBean getBean() {
		return bean;
	}
}
