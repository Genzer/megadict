package com.megadict.tts;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.megadict.model.Speaker;

public class MegaSpeaker implements Speaker {
	private final String TAG = "MegaSpeaker";
	private static final Locale DEFAULT_LOCALE = new Locale("en", "US");
	private Locale locale = DEFAULT_LOCALE;
	private final TextToSpeech tts;
	private final OnInitListener onInitListener;

	public MegaSpeaker(final Context context) {
		onInitListener = new OnInitListener() {
			@Override
			public void onInit(final int status) {
				if (status == TextToSpeech.SUCCESS) {
					final int result = tts.setLanguage(locale);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e(TAG, "Language is not available");
					} else {
						Log.d(TAG, "TTS engine has been successfully initialized.");
					}
				} else {
					Log.e(TAG, "Could not initialize TextToSpeech.");
				}
			}
		};
		tts = new TextToSpeech(context, onInitListener);
	}

	@Override
	public void speak(final String text) {
		tts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}

	public void shutDown() {
		tts.stop();
		tts.shutdown();
	}

	public void setLanguage(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public Locale getSupportedLanguage() {
		return null;
	}
}
