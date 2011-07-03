package com.megadict.exception;

@Deprecated
public class DictionaryNotFoundException extends Exception {
	public DictionaryNotFoundException() {
		super("Dictionary not found.");
	}
}
