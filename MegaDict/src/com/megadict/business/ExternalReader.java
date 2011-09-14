package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.megadict.exception.FileNotFoundException;
import com.megadict.model.DictionaryInformation;

public final class ExternalReader {
	private static final String TAG = "ExternalReader";
	private ExternalReader() {}

	/**
	 * Read from external storage.
	 * @param externalFile The folder stores dictionary files.
	 * @return List of dictionary information.
	 */
	public static List<DictionaryInformation> readExternalStorage(final File externalFile) {
		final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
		final File files[] = externalFile.listFiles();
		if (files == null) throw new IllegalArgumentException("External storage is not a valid directory.");
		for (final File file : files) {
			if (file.isDirectory()) {
				try {
					final DictionaryInformation info = DictionaryInformation.create(file);
					infos.add(info);
				} catch (final FileNotFoundException e) {
					Log.w(TAG, e.getMessage(), e);
				}
			}
		}
		return infos;
	}
}
