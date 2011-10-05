package com.megadict.wiki;

import java.util.ArrayList;
import java.util.List;

public final class Wiki {
	private static final String DEFAULT_COUNTRY_NAME = "England";
	private static final String DEFAULT_LANGUAGE_NAME = "English";
	private static final String DEFAULT_COUNTRY_CODE = "en";
	private static final List<Language> LANGUAGES = new ArrayList<Language>();
	static {
		final String[] languageNames = { "English", "German", "French" };
		final String[] countryNames = { "England", "Germany", "France" };
		final String[] countryCodes = { "en", "de", "fr" };
		for (int i = 0; i < languageNames.length; ++i) {
			LANGUAGES.add(new Language(countryNames[i], countryCodes[i], languageNames[i]));
		}
	}

	private Wiki() {}

	public static List<Language> getLanguages() {
		return LANGUAGES;
	}

	public static List<String> getLanguageNames() {
		final List<String> list = new ArrayList<String>();
		for (final Language lang : LANGUAGES) {
			list.add(lang.getLanguageName());
		}
		return list;
	}

	public static List<String> getCountryCodes() {
		final List<String> list = new ArrayList<String>();
		for (final Language lang : LANGUAGES) {
			list.add(lang.getCountryCode());
		}
		return list;
	}

	public static String getCountryNameByCountryCode(final String countryCode) {
		for (final Language l : LANGUAGES) {
			if (countryCode.equals(l.getCountryCode())) {
				return l.getCountryName();
			}
		}
		return DEFAULT_COUNTRY_NAME;
	}

	public static String getLanguageNameByCountryCode(final String countryCode) {
		for (final Language l : LANGUAGES) {
			if (countryCode.equals(l.getCountryCode())) {
				return l.getLanguageName();
			}
		}
		return DEFAULT_LANGUAGE_NAME;
	}

	public static String getCountryCodeByLanguageName(final String languageName) {
		for (final Language l : LANGUAGES) {
			if (languageName.equals(l.getLanguageName())) {
				return l.getCountryCode();
			}
		}
		return DEFAULT_COUNTRY_CODE;
	}

}
