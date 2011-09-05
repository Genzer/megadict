package com.megadict.speaker;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.megadict.model.Speaker;

public class TextToSpeechSpeaker implements Speaker {
	private final String TAG = "TextToSpeechSpeaker";
	private final TextToSpeech tts;

	public TextToSpeechSpeaker(final Context context, final Locale locale) {
		final OnInitListener onInitListener = new OnInitListener() {
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
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void shutDown() {
		tts.stop();
		tts.shutdown();
	}

	public void setLanguage(final Locale locale) {
		tts.setLanguage(locale);
	}

	@Override
	public Locale getSupportedLanguage() {
		return tts.getLanguage();
	}
}
