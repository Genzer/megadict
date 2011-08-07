package com.megadict.exception;

public class DataFileNotFoundException extends Exception {
	public DataFileNotFoundException(final String dictionaryPath) {
		super("Data file not found at " + dictionaryPath + ".");
	}
}
