package com.megadict.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.megadict.R;
import com.megadict.preferences.LanguagePreference;
import com.megadict.preferences.Preferences;

public class SettingActivity extends PreferenceActivity {
	private LanguagePreference languagePreference;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Must set preference file name before inflate preference screen.
		getPreferenceManager().setSharedPreferencesName(Preferences.NAME);
		// Inflate preference screen.
		addPreferencesFromResource(R.xml.main_pref);
		// Init language preference.
		languagePreference = LanguagePreference.newInstance(this);

		final ListPreference langListPref = (ListPreference)findPreference(Preferences.KEY_LANGUAGE);
		langListPref.setValue(languagePreference.getLanguage());
		langListPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				languagePreference.setLanguage(newValue.toString());
				return true;
			}
		});

		//		final ListPreference speakerLanguagePref = (ListPreference)findPreference(getString(R.string.speakerLanguagePrefKey));
		//		speakerLanguagePref.setEntries(R.array.displayLanguages);
		//		speakerLanguagePref.setEntryValues(R.array.languages);
		//		speakerLanguagePref.setDefaultValue(DEFAULT_LANGUAGE);
	}
}
