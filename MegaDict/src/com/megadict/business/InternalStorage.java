package com.megadict.business;

import java.io.File;

import android.content.Context;

public final class InternalStorage {
	private static final String HISTORY_FILE_NAME = "history";

	private InternalStorage() {}

	public static File getCacheDirectory(final Context context) {
		final File file = context.getCacheDir();
		final File cacheDirectory = new File(file.getAbsolutePath() + File.separator + HISTORY_FILE_NAME);
		return cacheDirectory;
	}


}
