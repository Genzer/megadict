package com.megadict.initializer;

import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
import com.megadict.exception.InternetNotConnectedException;
import com.megadict.exception.SpeakingFailedExeption;
import com.megadict.exception.UnsupportedLanguageException;
import com.megadict.preferences.SpeakerPreference;
import com.megadict.speaker.GoogleSpeaker;
import com.megadict.speaker.GoogleSpeaker.ExceptionHappenedListener;
import com.megadict.speaker.TextToSpeechSpeaker;
import com.megadict.utility.Utility;

public class PronounceButtonInitializer {
	private static final String TAG = "PronounceButtonInitializer";
	private final Context context;
	private final SpeakerPreference speakerPreference;
	private String speakerType;

	// Speakers.
	private final TextToSpeechSpeaker ttsSpeaker;
	private GoogleSpeaker googleSpeaker;

	public PronounceButtonInitializer(final DictionaryComponent dictionaryComponent) {
		this.context = dictionaryComponent.getContext();

		// Init speaker preference.
		speakerPreference = SpeakerPreference.newInstance(context);

		// Get speaker language from speaker preference.
		final Locale speakerLanguage = new Locale(speakerPreference.getSpeakerLanguage());

		// Init speakers.
		ttsSpeaker = createTextToSpeechSpeaker(speakerLanguage);
		googleSpeaker = createGoogleTranslateSpeaker(speakerLanguage);

		// Init the used speaker.
		initSpeakerBySpeakerType();

		dictionaryComponent.getPronounceButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Using TTS or Google translate here.
				final String pronounceWord = dictionaryComponent.getSearchBar().getText().toString();
				if(!"".equals(pronounceWord)) {
					doSpeaking(pronounceWord);
				}
			}
		});
	}

	private TextToSpeechSpeaker createTextToSpeechSpeaker(final Locale speakerLanguage) {
		return new TextToSpeechSpeaker(context, speakerLanguage);
	}

	private GoogleSpeaker createGoogleTranslateSpeaker(final Locale speakerLanguage) {
		final GoogleSpeaker speaker = new GoogleSpeaker(speakerLanguage, context);
		speaker.setExceptionHappendedListener(new ExceptionHappenedListener() {
			@Override
			public void exceptionHappended(final Throwable t) {
				if(t instanceof InternetNotConnectedException) {
					Utility.messageBox(context, R.string.internetNotConnected);
				} else if (t instanceof UnsupportedLanguageException) {
					Utility.messageBox(context, R.string.languageNotSupported);
				}
			}
		});
		return speaker;
	}

	public void updateSpeakerType() {
		Log.d(TAG, "updateSpeakerType");
		initSpeakerBySpeakerType();
	}

	private void initSpeakerBySpeakerType() {
		speakerType = speakerPreference.getSpeakerType();
	}

	public void updateSpeakerLanguage() {
		Log.d(TAG, "updateSpeakerLanguage");

		// Get speaker language from speaker preference.
		final Locale speakerLanguage = new Locale(speakerPreference.getSpeakerLanguage());
		// Update speaker language for GoogleSpeaker.
		googleSpeaker = createGoogleTranslateSpeaker(speakerLanguage);
		// Update speaker language for TTSSpeaker.
		ttsSpeaker.setLanguage(speakerLanguage);
	}

	private void doSpeaking(final String word) {
		if(speakerType.equals(SpeakerPreference.GOOGLE_TRANSLATE_SPEAKER_TYPE)) {
			googleSpeak(word);
		} else {
			ttsSpeak(word);
		}
	}

	private void ttsSpeak(final String word) {
		try {
			ttsSpeaker.speak(word);
		} catch (final UnsupportedLanguageException e) {
			Utility.messageBox(context, R.string.languageNotSupported);
		} catch (final SpeakingFailedExeption e) {
			Utility.messageBox(context, R.string.couldNotSpeakTTS);
		}
	}

	private void googleSpeak(final String word) {
		if(Utility.isOnline(context)) {
			googleSpeaker.speak(word);
		} else {
			Utility.messageBox(context, R.string.couldNotSpeakGoogle);
		}
	}

	public void shutDownTTSSpeaker() {
		ttsSpeaker.shutDown();
	}
}
