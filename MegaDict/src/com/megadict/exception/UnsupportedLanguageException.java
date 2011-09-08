package com.megadict.exception;


public class UnsupportedLanguageException extends RuntimeException {
	public UnsupportedLanguageException() {
		super();
	}

	public UnsupportedLanguageException(final String message) {
		super(message);
	}

	public UnsupportedLanguageException(final Throwable t) {
		super(t);
	}

	public UnsupportedLanguageException(final String message, final Throwable t) {
		super(message, t);
	}
}
