package com.megadict.business;

import java.io.File;

import android.os.Environment;

import com.megadict.exception.CouldNotCreateExternalDirectoryException;

public final class ExternalStorage {
	private final static String DOWNLOAD_DIRECTORY = "Download";
	private final static String MEGADICT_DIRECTORY = "megadict";

	private ExternalStorage(){}

	public static File getExternalDirectory() {
		if (!isExternalStorageAvailable() && isExternalStorageReadOnly()) {
			throw new CouldNotCreateExternalDirectoryException();
		}

		/* Only use this in API 2.2 */
		//final File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

		/* Absolute path for every API */
		//final File downloadDirectory = new File("/sdcard/Download");

		final File downloadDirectory = new File(
				Environment.getExternalStorageDirectory().getAbsolutePath() +
				File.separator + DOWNLOAD_DIRECTORY);
		// Create "Download" directory if it doesn't exist.
		if (!downloadDirectory.exists()) {
			downloadDirectory.mkdir();
		}

		final File externalDirectory = new File(downloadDirectory.getAbsolutePath() +
				File.separator	+ MEGADICT_DIRECTORY);
		// Create megadict directory if it doesn't exits.
		if (!externalDirectory.exists()) {
			externalDirectory.mkdir();
		}

		return externalDirectory;
	}

	private static boolean isExternalStorageAvailable() {
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	private static boolean isExternalStorageReadOnly() {
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
}
