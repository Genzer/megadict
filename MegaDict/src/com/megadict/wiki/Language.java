package com.megadict.wiki;

public class Language {
	private final String countryName;
	private final String countryCode;
	private final String languageName;

	public Language(final String countryName, final String countryCode, final String languageName) {
		this.countryName = countryName;
		this.countryCode = countryCode;
		this.languageName = languageName;
	}

	public String getLanguageName() {
		return languageName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCountryName() {
		return countryName;
	}
}
