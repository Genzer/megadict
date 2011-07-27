package com.megadict.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.megadict.R;

public class SettingActivity extends PreferenceActivity {
	private final String LANGUAGE_PREF = "languagePrefKey";
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_pref);
		final ListPreference languagePref = (ListPreference)findPreference(getString(R.string.languagePrefKey));

		languagePref.setValue("en");
		languagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference pref, final Object newValue) {
				final SharedPreferences.Editor editor = languagePref.getEditor();
				editor.putString(LANGUAGE_PREF, newValue.toString());
				editor.commit();
				return true;
			}
		});
	}
}
