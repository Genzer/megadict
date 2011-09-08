package com.megadict.speaker;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.megadict.exception.SpeakingFailedExeption;
import com.megadict.exception.UnsupportedLanguageException;

public class TextToSpeechSpeaker {
	private static final String TAG = "TextToSpeechSpeaker";
	private final TextToSpeech tts;
	private Locale locale;

	public TextToSpeechSpeaker(final Context context, final Locale locale) {
		this.locale = locale;
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

	/**
	 * @throws UnsupportedLanguageException if the spoken language is not supported.
	 * @throws SpeakingFailedExeption if the speaking failed.
	 */
	public void speak(final String text) {
		final int r = tts.setLanguage(locale);
		if(r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
			throw new UnsupportedLanguageException();
		} else {
			if(tts.speak(text, TextToSpeech.QUEUE_FLUSH, null) == TextToSpeech.ERROR) {
				throw new SpeakingFailedExeption();
			}
		}
	}

	public void shutDown() {
		tts.stop();
		tts.shutdown();
	}

	public void setLanguage(final Locale locale) {
		this.locale = locale;
	}

	public Locale getLanguage() {
		return locale;
	}
}
