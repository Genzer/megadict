package com.megadict.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.megadict.R;
import com.megadict.utility.Utility;

public class SettingActivity extends PreferenceActivity {
	private final String DEFAULT_LANGUAGE = "en";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_pref);

		final String locale = initLocale();
		// These lines are used for selecting language preference, nothing more.
		final ListPreference languagePref =
				(ListPreference) findPreference(getString(R.string.languagePrefKey));
		languagePref.setDefaultValue(locale);
		languagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference pref, final Object newValue) {
				Utility.setLocale(SettingActivity.this, newValue.toString());
				Utility.setLocaleChanged(true);
				return true;
			}
		});

		//		final ListPreference speakerLanguagePref = (ListPreference)findPreference(getString(R.string.speakerLanguagePrefKey));
		//		speakerLanguagePref.setEntries(R.array.displayLanguages);
		//		speakerLanguagePref.setEntryValues(R.array.languages);
		//		speakerLanguagePref.setDefaultValue(DEFAULT_LANGUAGE);
	}

	private String initLocale() {
		//		final String locale = Utility.getLocale(getBaseContext());
		final String locale = Utility.getLocale(this);
		Utility.setLocale(getBaseContext(), locale);
		return locale;
	}
}
