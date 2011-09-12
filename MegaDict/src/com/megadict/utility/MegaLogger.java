package com.megadict.utility;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public final class MegaLogger {
	// Init logger for debugging.
	private static final Logger LOGGER = Logger.getLogger("MegaLogger");
	static {
		LOGGER.addHandler(new ConsoleHandler());
	}

	private MegaLogger() {}

	public static void log(final String message) {
		LOGGER.warning(message);
	}
}
