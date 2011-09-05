package com.megadict.speaker;

import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

public class GoogleSpeaker extends GoogleTranslateSpeaker {
	public GoogleSpeaker(final Locale language, final Context context) {
		super(language, context);
	}

	@Override
	public void speak(final String text) {
		final SpeakTask task = new SpeakTask();
		task.execute(text);
	}

	private class SpeakTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(final String... params) {
			GoogleSpeaker.super.speak(params[0]);
			return null;
		}
	}
}
