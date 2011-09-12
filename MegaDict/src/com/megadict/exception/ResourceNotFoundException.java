package com.megadict.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(final String message) {
		super(message);
	}

	public ResourceNotFoundException(final Throwable t) {
		super(t);
	}

	public ResourceNotFoundException(final String message, final Throwable t) {
		super(message, t);
	}
}
