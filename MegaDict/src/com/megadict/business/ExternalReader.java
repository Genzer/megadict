package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.model.DictionaryInformation;

public class ExternalReader {
	private final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
	private final List<String> logger = new ArrayList<String>();

	public static final String NO_DICTIONARY = "There is no dictionary.";
	public static final String INDEX_FILE_NOT_FOUND = "Index file not found.";
	public static final String DATA_FILE_NOT_FOUND = "Data file not found.";

	public List<DictionaryInformation> getInfos() {
		return infos;
	}

	public List<String> getLogger() {
		return logger;
	}

	public ExternalReader(final File externalFile) {
		init(externalFile);
	}

	private void init(final File externalFile) {
		final File files[] = externalFile.listFiles();
		if (files == null) throw new IllegalArgumentException("External storage is not a valid directory.");

		DictionaryInformation info = null;

		for (final File file : files) {
			if (file.isDirectory()) {
				final String locationInfo = " Location: " + file.getAbsolutePath();

				try {
					info = createDictionaryInformation(file);
				} catch (final IndexFileNotFoundException e) {
					logger.add(INDEX_FILE_NOT_FOUND + locationInfo);
				} catch (final DataFileNotFoundException e) {
					logger.add(DATA_FILE_NOT_FOUND + locationInfo);
				}

				// If index file or data file are not found, just silently ignore it.
				if(info == null) continue;

				// Store bean and info for later use.
				infos.add(info);
			}
		}
	}

	private DictionaryInformation createDictionaryInformation(final File file) throws IndexFileNotFoundException, DataFileNotFoundException {
		return new DictionaryInformation(file);
	}
}
