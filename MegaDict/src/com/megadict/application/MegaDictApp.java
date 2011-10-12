package com.megadict.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.preferences.LanguagePreference;
import com.megadict.utility.Utility;

public final class MegaDictApp extends Application {
	public static final String TAG = "MegaDictApp";
	public DictionaryScanner scanner;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

		// Prepare application variables.
		scanner = new DictionaryScanner(this);
		scanner.scanStorage();

		// Load language when first start app.
		Utility.updateLocale(getBaseContext(), LanguagePreference.newInstance(this).getLanguage());
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d(TAG, "onConfigurationChanged");
		Utility.updateLocale(getBaseContext(), LanguagePreference.newInstance(this).getLanguage());
	}
}
