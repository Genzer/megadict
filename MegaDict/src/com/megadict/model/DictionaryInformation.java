package com.megadict.model;

import java.io.File;

import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;

public class DictionaryInformation {
	private final File parentFile;
	private File indexFile;
	private File dataFile;

	public DictionaryInformation(final File parentFilePath) throws IndexFileNotFoundException, DataFileNotFoundException {
		this.parentFile = parentFilePath;
		createNecessaryFiles(parentFile);
	}

	private void createNecessaryFiles(final File parentFilePath) throws IndexFileNotFoundException, DataFileNotFoundException {

		/* Create index and data file path. */
		final File filePath = parentFilePath;
		final String separator = System.getProperty("file.separator");
		final String indexFilePath =
			filePath + separator + "dict.index";
		final String dataFilePath =
			filePath + separator + "dict.dict";

		/* Check if index file and data file exist. If not, throw exception. */
		indexFile = new File(indexFilePath);
		dataFile = new File(dataFilePath);

		if (!indexFile.exists()) {
			throw new IndexFileNotFoundException();
		}
		if (!dataFile.exists()) {
			throw new DataFileNotFoundException();
		}
	}

	public File getParentFile() {
		return parentFile;
	}

	public File getIndexFile() {
		return indexFile;
	}

	public File getDataFile() {
		return dataFile;
	}
}
