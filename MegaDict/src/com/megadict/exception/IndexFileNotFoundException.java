package com.megadict.exception;

public class IndexFileNotFoundException extends Exception {
	public IndexFileNotFoundException() {
		super("Index file not found.");
	}
}
