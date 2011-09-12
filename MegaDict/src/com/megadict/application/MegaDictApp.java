/**
 * IDICTIONARY APPLICATION.
 * 
 * CREATION DATE 10-April-2011
 * 
 * DEVELOPER TEAM :PREPOOLK11 - IDICTGROUP
 */

package com.megadict.application;

import android.app.Application;

import com.megadict.business.ExternalStorage;
import com.megadict.business.ResultTextMaker;
import com.megadict.business.scanning.DictionaryScanner;

public final class MegaDictApp extends Application {
	public DictionaryScanner scanner;
	public ResultTextMaker resultTextMaker;

	@Override
	public void onCreate() {
		super.onCreate();
		// Call this to create megadict folder.
		ExternalStorage.getExternalDirectory();

		// Prepare application variables.
		resultTextMaker = new ResultTextMaker(getAssets());
		scanner = new DictionaryScanner(resultTextMaker);
	}
}
