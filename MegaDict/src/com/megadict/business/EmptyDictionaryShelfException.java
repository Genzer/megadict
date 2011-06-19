package com.megadict.business;

public class EmptyDictionaryShelfException extends Exception {
	public EmptyDictionaryShelfException() {
		super("Dictionary shelf is empty.");
	}
}
