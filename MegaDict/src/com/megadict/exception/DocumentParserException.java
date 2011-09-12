package com.megadict.exception;

public class DocumentParserException extends Exception {
	public DocumentParserException() {
		super();
	}

	public DocumentParserException(final String message) {
		super(message);
	}

	public DocumentParserException(final Throwable t) {
		super(t);
	}

	public DocumentParserException(final String message, final Throwable t) {
		super(message, t);
	}
}
