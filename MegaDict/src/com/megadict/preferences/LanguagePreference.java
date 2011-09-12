package com.megadict.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public final class LanguagePreference {
	// Language preferences.
	public static final String KEY_LANGUAGE = "megadict.language";
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String LANGUAGE_CHANGED = "languageChanged";

	// Useful variables.
	private static LanguagePreference pref;
	private final SharedPreferences sharedPref;

	private LanguagePreference(final Context context) {
		sharedPref = context.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
	}

	public static LanguagePreference newInstance(final Context context) {
		if(pref == null) {
			pref = new LanguagePreference(context);
		}
		return pref;
	}

	public String getLanguage() {
		return sharedPref.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
	}

	public void setLanguage(final String language) {
		sharedPref.edit().putString(KEY_LANGUAGE, language).commit();
	}
}
