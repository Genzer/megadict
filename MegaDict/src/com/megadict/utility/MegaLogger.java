package com.megadict.utility;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class MegaLogger {
	// Init logger for debugging.
	private static final Logger LOGGER = Logger.getLogger("DictionaryScanner");
	static { LOGGER.addHandler(new ConsoleHandler()); }

	public static void log(final String message) {
		LOGGER.warning(message);
	}
}
