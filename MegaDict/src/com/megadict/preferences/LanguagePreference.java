package com.megadict.preferences;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public final class LanguagePreference {
	private static LanguagePreference pref;
	private static boolean languageChanged;

	private String oldLanguage;
	private final SharedPreferences sharedPref;
	private final Context context;

	private LanguagePreference(final Context context) {
		this.context = context;
		sharedPref = context.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
		oldLanguage = getLanguage();
	}

	public static LanguagePreference newInstance(final Context context) {
		if(pref == null) {
			return new LanguagePreference(context);
		}
		return pref;
	}

	public String getLanguage() {
		final String value = sharedPref.getString(Preferences.KEY_LANGUAGE, Preferences.DEFAULT_LANGUAGE);
		return value;
	}

	public void setLanguage(final String language) {
		if(!oldLanguage.equals(language)) {
			sharedPref.edit().putString(Preferences.KEY_LANGUAGE, language).commit();
			updateLocale(language);
			languageChanged = true;
			oldLanguage = language;
		}
	}

	private void updateLocale(final String language) {
		final Locale locale = new Locale(language);
		Locale.setDefault(locale);
		final Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	}

	public boolean isLanguageChanged() {
		return languageChanged;
	}

	public void resetLanguageChanged() {
		languageChanged = false;
	}
}
