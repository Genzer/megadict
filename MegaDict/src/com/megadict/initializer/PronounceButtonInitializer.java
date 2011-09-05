package com.megadict.initializer;

import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.megadict.bean.DictionaryComponent;
import com.megadict.model.Speaker;
import com.megadict.preferences.SpeakerPreference;
import com.megadict.speaker.GoogleSpeaker;
import com.megadict.speaker.TextToSpeechSpeaker;

public class PronounceButtonInitializer {
	private final Context context;
	private final SpeakerPreference speakerPreference;

	// Speakers.
	private final TextToSpeechSpeaker ttsSpeaker;
	private GoogleSpeaker googleSpeaker;
	private Speaker speaker;

	public PronounceButtonInitializer(final DictionaryComponent dictionaryComponent) {
		this.context = dictionaryComponent.getContext();

		// Init speaker preference.
		speakerPreference = SpeakerPreference.newInstance(context);

		// Init speakers.
		ttsSpeaker = createTextToSpeechSpeaker();
		googleSpeaker = createGoogleTranslateSpeaker();

		// Init the used speaker.
		initSpeakerBySpeakerType();

		dictionaryComponent.getPronounceButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Using TTS or Google translate here.
				final String pronounceWord = dictionaryComponent.getSearchBar().getText().toString();
				if(!"".equals(pronounceWord)) {
					speaker.speak(pronounceWord);
				}
			}
		});
	}

	private TextToSpeechSpeaker createTextToSpeechSpeaker() {
		return new TextToSpeechSpeaker(context, new Locale(speakerPreference.getSpeakerLanguage()));
	}

	private GoogleSpeaker createGoogleTranslateSpeaker() {
		return new GoogleSpeaker(new Locale(speakerPreference.getSpeakerLanguage()), context);
	}

	public void updateSpeakerType() {
		initSpeakerBySpeakerType();
	}

	private void initSpeakerBySpeakerType() {
		final String speakerType = speakerPreference.getSpeakerType();
		if(speakerType.equals(SpeakerPreference.GOOGLE_TRANSLATE_SPEAKER_TYPE)) {
			speaker = googleSpeaker;
		} else {
			speaker = ttsSpeaker;
		}
	}

	public void updateSpeakerLanguage() {
		final String speakerLanguage = speakerPreference.getSpeakerLanguage();
		// Update languages of all speakers.
		googleSpeaker = new GoogleSpeaker(new Locale(speakerLanguage), context);
		ttsSpeaker.setLanguage(new Locale(speakerLanguage));
		// If speaker is GoogleSpeaker,
		/// we must set speaker to its new reference because googleSpeaker was recreated.
		if(speaker instanceof GoogleSpeaker) {
			speaker = googleSpeaker;
		}
	}

	public void shutDownTTSSpeaker() {
		ttsSpeaker.shutDown();
	}
}
