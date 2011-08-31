package com.megadict.initializer;

import android.view.View;
import android.view.View.OnClickListener;

import com.megadict.bean.DictionaryComponent;
import com.megadict.tts.MegaSpeaker;

public class PronounceButtonInitializer {
	MegaSpeaker megaSpeaker;
	public PronounceButtonInitializer(final DictionaryComponent dictionaryComponent) {
		// Init MegaSpeaker.
		megaSpeaker = new MegaSpeaker(dictionaryComponent.getContext());

		dictionaryComponent.getPronounceButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Using TTS or Google translate here.
				final String pronounceWord = dictionaryComponent.getSearchBar().getText().toString();
				if(!"".equals(pronounceWord)) {
					megaSpeaker.speak(pronounceWord);
				}
			}
		});
	}

	public void shutDownMegaSpeaker() {
		megaSpeaker.shutDown();
	}
}
