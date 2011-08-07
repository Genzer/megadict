package com.megadict.exception;

public class IndexFileNotFoundException extends Exception {
	public IndexFileNotFoundException(final String dictionaryPath) {
		super("Index file not found at " + dictionaryPath + ".");
	}
}
