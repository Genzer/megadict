package com.megadict.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public final class SpeakerPreference {
	// Speaker type preferences.
	public static final String KEY_SPEAKER_TYPE = "megadict.speakerType";
	public static final String GOOGLE_TRANSLATE_SPEAKER_TYPE = "googleTranslate";
	public static final String TEXT_TO_SPEECH_SPEAKER_TYPE = "textToSpeech";
	public static final String DEFAULT_SPEAKER_TYPE = GOOGLE_TRANSLATE_SPEAKER_TYPE;

	// utitlity variables.
	public static final String SPEAKER_TYPE_CHANGED = "speakerTypeChanged";
	public static final String SPEAKER_LANGUAGE_CHANGED = "speakerTypeChanged";

	// Speaker language prefrences.
	public static final String KEY_SPEAKER_LANGUAGE = "megadict.speakerLanguage";
	public static final String DEFAULT_SPEAKER_LANGUAGE = "en";

	// Useful variables.
	private static SpeakerPreference speakerPreference;
	private final SharedPreferences sharedPref;

	private SpeakerPreference(final Context context) {
		sharedPref = context.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
	}

	public String getSpeakerType() {
		return sharedPref.getString(KEY_SPEAKER_TYPE, DEFAULT_SPEAKER_TYPE);
	}

	public void setSpeakerType(final String speakerType) {
		sharedPref.edit().putString(KEY_SPEAKER_TYPE, speakerType);
	}

	public String getSpeakerLanguage() {
		return sharedPref.getString(KEY_SPEAKER_LANGUAGE, DEFAULT_SPEAKER_LANGUAGE);
	}

	public void setSpeakerLanguage(final String speakerLanguage) {
		sharedPref.edit().putString(KEY_SPEAKER_LANGUAGE, speakerLanguage);
	}

	public static SpeakerPreference newInstance(final Context context) {
		if(speakerPreference == null) {
			return new SpeakerPreference(context);
		}
		return speakerPreference;
	}
}
