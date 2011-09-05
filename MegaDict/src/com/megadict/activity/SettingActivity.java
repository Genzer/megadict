package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.megadict.preferences.SpeakerPreference;
import com.megadict.utility.Utility;

public class SettingActivity extends PreferenceActivity {
	private LanguagePreference languagePreference;
	private SpeakerPreference speakerPreference;
	private final List<Preference> preferences = new ArrayList<Preference>();
	private final Intent returnedIntent = new Intent();

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
		// Init speaker preference.
		speakerPreference = SpeakerPreference.newInstance(this);

		// Display language preference.
		final ListPreference langListPref = (ListPreference)findPreference(LanguagePreference.KEY_LANGUAGE);
		langListPref.setValue(languagePreference.getLanguage());
		langListPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				if(!languagePreference.getLanguage().equals(newValue)) {
					languagePreference.setLanguage(newValue.toString());
					Utility.updateLocale(SettingActivity.this, newValue.toString());
					// Update preference text.
					updatePreferenceTexts();
					returnedIntent.putExtra(LanguagePreference.LANGUAGE_CHANGED, true);
				}
				return true;
			}
		});

		// Speaker type preference.
		final ListPreference speakerTypePref = (ListPreference)findPreference(SpeakerPreference.KEY_SPEAKER_TYPE);
		speakerTypePref.setValue(speakerPreference.getSpeakerType());
		speakerTypePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				if(!speakerPreference.getSpeakerType().equals(newValue)) {
					speakerPreference.setSpeakerType(newValue.toString());
					returnedIntent.putExtra(SpeakerPreference.SPEAKER_TYPE_CHANGED, true);
				}
				return true;
			}
		});

		// Speaker language preference.
		final ListPreference speakerLanguagePref = (ListPreference)findPreference(SpeakerPreference.KEY_SPEAKER_LANGUAGE);
		speakerLanguagePref.setValue(speakerPreference.getSpeakerLanguage());
		speakerLanguagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				if(!speakerPreference.getSpeakerLanguage().equals(newValue)) {
					speakerPreference.setSpeakerLanguage(newValue.toString());
					returnedIntent.putExtra(SpeakerPreference.SPEAKER_LANGUAGE_CHANGED, true);
				}
				return true;
			}
		});

		// Set this to return intent to its owner.
		setResult(Activity.RESULT_OK, returnedIntent);
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

	@Override
	protected void onStop() {
		super.onStop();
		finish();
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
