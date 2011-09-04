package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

import com.megadict.R;
import com.megadict.preferences.LanguagePreference;
import com.megadict.preferences.Preferences;

public class SettingActivity extends PreferenceActivity {
	private LanguagePreference languagePreference;
	private final List<Preference> preferences = new ArrayList<Preference>();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Must set preference file name before inflate preference screen.
		getPreferenceManager().setSharedPreferencesName(Preferences.NAME);
		// Inflate preference screen.
		addPreferencesFromResource(R.xml.main_pref);
		// Store preferences.
		storePreferences();

		// Init language preference.
		languagePreference = LanguagePreference.newInstance(this);

		final ListPreference langListPref = (ListPreference)findPreference(Preferences.KEY_LANGUAGE);
		langListPref.setValue(languagePreference.getLanguage());
		langListPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				languagePreference.setLanguage(newValue.toString());
				// Update preference text.
				updatePreferenceTexts();
				return true;
			}
		});

		//		final ListPreference speakerLanguagePref = (ListPreference)findPreference(getString(R.string.speakerLanguagePrefKey));
		//		speakerLanguagePref.setEntries(R.array.displayLanguages);
		//		speakerLanguagePref.setEntryValues(R.array.languages);
		//		speakerLanguagePref.setDefaultValue(DEFAULT_LANGUAGE);
	}

	private void updatePreferenceTexts() {
		final String []titles = getResources().getStringArray(R.array.preferenceTitles);
		final String []summaries = getResources().getStringArray(R.array.preferenceSummaries);
		int index = 0;
		for(final Preference pref : preferences) {
			pref.setTitle(titles[index]);
			pref.setSummary(summaries[index]);
			if(pref instanceof DialogPreference) {
				((DialogPreference) pref).setDialogTitle(titles[index]);
			}
			++index;
		}
	}

	private void storePreferences() {
		final PreferenceGroup rootPref = getPreferenceScreen();
		callRecursion(rootPref);
	}

	private void callRecursion(final Preference pref) {
		preferences.add(pref);
		if(pref instanceof PreferenceGroup) {
			final PreferenceGroup prefGroup = (PreferenceGroup)pref;
			for(int i = 0; i < prefGroup.getPreferenceCount(); ++i) {
				callRecursion(prefGroup.getPreference(i));
			}
		}
	}
}
