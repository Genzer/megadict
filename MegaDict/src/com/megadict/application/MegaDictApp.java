package com.megadict.application;

import android.app.Application;

import com.megadict.business.ExternalStorage;
import com.megadict.business.scanning.DictionaryScanner;

public final class MegaDictApp extends Application {
	public DictionaryScanner scanner;

	@Override
	public void onCreate() {
		super.onCreate();
		// Call this to create megadict folder.
		ExternalStorage.getExternalDirectory();

		// Prepare application variables.
		scanner = new DictionaryScanner(this);
		scanner.scanStorage();
	}
}
