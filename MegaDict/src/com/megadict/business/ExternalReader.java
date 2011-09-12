package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.megadict.exception.FileNotFoundException;
import com.megadict.model.DictionaryInformation;
import com.megadict.utility.MegaLogger;

public class ExternalReader {
	public static List<DictionaryInformation> readExternalStorage(final File externalFile) {
		final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
		final File files[] = externalFile.listFiles();
		if (files == null)
			throw new IllegalArgumentException("External storage is not a valid directory.");
		for (final File file : files) {
			if (file.isDirectory()) {
				try {
					final DictionaryInformation info = createDictionaryInformation(file);
					infos.add(info);
				} catch (final FileNotFoundException e) {
					MegaLogger.log(e.getMessage());
				}
			}
		}
		return infos;
	}

	private static DictionaryInformation createDictionaryInformation(final File parentFilePath) {
		return new DictionaryInformation(parentFilePath);
	}
}
