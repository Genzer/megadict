package com.megadict.exception;

public class FileNotFoundException extends RuntimeException {
	private static final String NOT_FOUND_MESSAGE = "%s file not found at %s.";

	public FileNotFoundException(final String fileName, final String location, final Throwable t) {
		super(makeMessage(fileName, location), t);
	}

	public FileNotFoundException(final String fileName, final String location) {
		super(makeMessage(fileName, location));
	}

	private static String makeMessage(final String fileName, final String location) {
		return String.format(NOT_FOUND_MESSAGE, fileName, location);
	}
}
