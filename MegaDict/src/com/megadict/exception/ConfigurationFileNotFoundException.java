package com.megadict.exception;

public class ConfigurationFileNotFoundException extends Exception {
	public ConfigurationFileNotFoundException() {
		super("Configuration file not found.");
	}
}
