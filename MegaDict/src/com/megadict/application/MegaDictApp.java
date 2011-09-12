/**
 * IDICTIONARY APPLICATION.
 * 
 * CREATION DATE 10-April-2011
 * 
 * DEVELOPER TEAM :PREPOOLK11 - IDICTGROUP
 */

package com.megadict.application;

import android.app.Application;

import com.megadict.business.scanning.DictionaryScanner;

public final class MegaDictApp extends Application {
	public DictionaryScanner scanner;

	@Override
	public void onCreate() {
		super.onCreate();
		scanner = new DictionaryScanner(getApplicationContext());
	}
}
