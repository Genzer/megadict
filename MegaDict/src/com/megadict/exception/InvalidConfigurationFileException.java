package com.megadict.exception;

@Deprecated
public class InvalidConfigurationFileException extends Exception {
	public InvalidConfigurationFileException() {
		super("Invalid configuration file.");
	}
}
