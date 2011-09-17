package com.megadict.model;

import java.io.File;

import com.megadict.exception.FileNotFoundException;

public class DictionaryInformation {
	private final File parentFile;
	private File indexFile;
	private File dataFile;

	public DictionaryInformation(final File parentFilePath) {
		this.parentFile = parentFilePath;
		createNecessaryFiles(parentFile);
	}

	public DictionaryInformation(final String parentFilePath) {
		this(new File(parentFilePath));
	}

	private void createNecessaryFiles(final File parentFilePath) {

		/* Create index and data file path. */
		final String filePath = parentFilePath.getAbsolutePath();
		indexFile = new File(filePath + File.separator + "dict.index");
		dataFile = new File(filePath + File.separator + "dict.dict");

		/* Check if index file and data file exist. If not, throw exception. */
		if (!indexFile.exists()) {
			throw new FileNotFoundException("Index", filePath);
		}
		if (!dataFile.exists()) {
			throw new FileNotFoundException("Data", filePath);
		}
	}

	/**
	 * Create new DictionaryInformation.
	 * @param parentFilePath File
	 * @throws FileNotFoundException if index file or data file not found.
	 * @return DictionaryInformation
	 */
	public static DictionaryInformation create(final File parentFilePath) {
		return new DictionaryInformation(parentFilePath);
	}

	/**
	 * Create new DictionaryInformation.
	 * @param parentFilePath String
	 * @throws FileNotFoundException if index file or data file not found.
	 * @return DictionaryInformation
	 */
	public static DictionaryInformation create(final String parentFilePath) {
		return new DictionaryInformation(parentFilePath);
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
