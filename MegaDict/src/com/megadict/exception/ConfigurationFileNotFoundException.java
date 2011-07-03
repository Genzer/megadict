package com.megadict.exception;

@Deprecated
public class ConfigurationFileNotFoundException extends Exception {
	public ConfigurationFileNotFoundException() {
		super("Configuration file not found.");
	}
}
