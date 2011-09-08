package com.megadict.exception;

public class SpeakingFailedExeption extends RuntimeException {
	public SpeakingFailedExeption() {
		super();
	}

	public SpeakingFailedExeption(final String message) {
		super(message);
	}

	public SpeakingFailedExeption(final Throwable t) {
		super(t);
	}

	public SpeakingFailedExeption(final String message, final Throwable t) {
		super(message, t);
	}
}
