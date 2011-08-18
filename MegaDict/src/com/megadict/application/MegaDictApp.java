/**
 * IDICTIONARY APPLICATION.
 * 
 * CREATION DATE 10-April-2011
 * 
 * DEVELOPER TEAM :PREPOOLK11 - IDICTGROUP
 */

package com.megadict.application;

import java.io.File;

import android.app.Application;

import com.megadict.business.ExternalStorage;
import com.megadict.business.scanning.DictionaryScanner;

public final class MegaDictApp extends Application {
	public final File externalStorage = ExternalStorage.getExternalDirectory();
	public final DictionaryScanner scanner = new DictionaryScanner();

	@Override
	public void onCreate() {
		super.onCreate();
	}


}
