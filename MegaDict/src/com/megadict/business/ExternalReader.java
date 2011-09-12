package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.megadict.exception.FileNotFoundException;
import com.megadict.model.DictionaryInformation;
import com.megadict.utility.MegaLogger;

public final class ExternalReader {
	private ExternalReader() {}

	/**
	 * Read from external storage.
	 * @param externalFile The folder stores dictionary files.
	 * @return List of dictionary information.
	 */
	public static List<DictionaryInformation> readExternalStorage(final File externalFile) {
		final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
		final File files[] = externalFile.listFiles();
		if (files == null)
			throw new IllegalArgumentException("External storage is not a valid directory.");
		for (final File file : files) {
			if (file.isDirectory()) {
				try {
					final DictionaryInformation info = DictionaryInformation.create(file);
					infos.add(info);
				} catch (final FileNotFoundException e) {
					MegaLogger.log(e.getMessage());
				}
			}
		}
		return infos;
	}
}
