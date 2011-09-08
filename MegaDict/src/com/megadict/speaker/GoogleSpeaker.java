package com.megadict.speaker;

import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.megadict.exception.InternetConnectionFailedException;
import com.megadict.exception.InternetNotConnectedException;
import com.megadict.exception.OperationFailedException;
import com.megadict.exception.UnsupportedLanguageException;

public class GoogleSpeaker extends GoogleTranslateSpeaker {
	private ExceptionHappenedListener exceptionHappendedListener;

	public GoogleSpeaker(final Locale language, final Context context) {
		super(language, context);
	}

	/**
	 * @throws InternetNotConnectedException if Internet is not connected.
	 * @throws UnsupportedLanguageException if the spoken language is not supported.
	 */
	@Override
	public void speak(final String text) {
		final SpeakTask task = new SpeakTask();
		task.setExeptionHappendedListener(exceptionHappendedListener);
		task.execute(text);
	}

	private class SpeakTask extends AsyncTask<String, Void, Throwable> {
		private ExceptionHappenedListener exceptionHappendedListener;

		@Override
		protected Throwable doInBackground(final String... params) {
			try {
				GoogleSpeaker.super.speak(params[0]);
			} catch(final InternetConnectionFailedException e) {
				return new InternetNotConnectedException(e);
			} catch(final OperationFailedException e) {
				return new UnsupportedLanguageException(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Throwable result) {
			super.onPostExecute(result);
			if(result != null && exceptionHappendedListener != null) {
				exceptionHappendedListener.exceptionHappended(result);
			}
		}

		public void setExeptionHappendedListener(final ExceptionHappenedListener listener) {
			this.exceptionHappendedListener = listener;
		}
	}

	public void setExceptionHappendedListener(final ExceptionHappenedListener listener) {
		this.exceptionHappendedListener = listener;
	}

	public interface ExceptionHappenedListener {
		void exceptionHappended(Throwable t);
	}
}
