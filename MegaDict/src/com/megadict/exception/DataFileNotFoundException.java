package com.megadict.exception;

public class DataFileNotFoundException extends Exception {
	public DataFileNotFoundException() {
		super("Data file not found.");
	}
}
