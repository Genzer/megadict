package com.megadict.business;

import java.io.File;

import android.os.Environment;

import com.megadict.exception.CouldNotCreateExternalDirectoryException;

public class ExternalDirectoryCreator {
	public static File createDirectory() throws CouldNotCreateExternalDirectoryException, SecurityException
	{
		if(!ExternalStorage.isExternalStorageAvailable() &&
				ExternalStorage.isExternalStorageReadOnly())
			throw new CouldNotCreateExternalDirectoryException();

		final File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		// Create "Download" directory if it doesn't exist.
		if(!downloadDirectory.exists()) {
			downloadDirectory.mkdir();
		}

		final File externalDirectory = new File(downloadDirectory.getAbsolutePath() + "/megadict");
		// Create megadict directory if it doesn't exits.
		if(!externalDirectory.exists()) {
			externalDirectory.mkdir();
		}

		return externalDirectory;
	}
}
