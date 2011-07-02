package com.megadict.exception;

public class CouldNotCreateExternalDirectoryException extends RuntimeException {
	public CouldNotCreateExternalDirectoryException() {
		super("Could not create external directory.");
	}
}
