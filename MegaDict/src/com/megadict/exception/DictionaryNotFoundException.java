package com.megadict.exception;

public class DictionaryNotFoundException extends Exception {
	public DictionaryNotFoundException() {
		super("Dictionary not found.");
	}
}
