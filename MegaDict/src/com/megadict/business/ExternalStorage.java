package com.megadict.business;

import android.os.Environment;

public abstract class ExternalStorage {

	public static boolean isExternalStorageAvailable() {
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public static boolean isExternalStorageReadOnly() {
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
}
