package com.megadict.activity;

import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.megadict.R;
import com.megadict.utility.Utility;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_pref);

		final String locale = initLocale();
		// These lines are used for selecting language preference, nothing more.
		final DialogPreference languagePref =
				(DialogPreference) findPreference(getString(R.string.languagePrefKey));
		languagePref.setDefaultValue(locale);
		languagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(final Preference pref, final Object newValue) {
				Utility.setLocale(getBaseContext(), newValue.toString());
				Utility.setLocaleChanged(true);
				return true;
			}
		});
	}

	private String initLocale() {
		final String locale = Utility.getLocale(getBaseContext());
		Utility.setLocale(getBaseContext(), locale);
		return locale;
	}
}
