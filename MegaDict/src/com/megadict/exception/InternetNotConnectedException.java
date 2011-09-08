package com.megadict.exception;

public class InternetNotConnectedException extends RuntimeException {
	public InternetNotConnectedException() {
		super();
	}

	public InternetNotConnectedException(final String message) {
		super(message);
	}

	public InternetNotConnectedException(final Throwable t) {
		super(t);
	}

	public InternetNotConnectedException(final String message, final Throwable t) {
		super(message, t);
	}
}
