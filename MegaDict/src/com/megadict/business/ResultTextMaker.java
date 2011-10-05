package com.megadict.business;


public final class ResultTextMaker {
	public static final String ASSET_URL = "file:///android_asset/";

	private static final String RESULT_HTML =
			"<html>" +
					"<head>" +
					"<link href=\"css/result.css\" rel=\"stylesheet\" type=\"text/css\"/>" +
					"<script src=\"scripts/result.js\" type=\"text/javascript\"/></script>" +
					"</head>" +
					"<body>%s</body>" +
					"</html>";

	private static final String CONTENT_FAILURE_HTML =
			"<div class=\"dictionaryBlock\">" +
					"<div class=\"searchedWord\">%s</div>" +
					"<div class=\"dictionaryContentFailure\">%s</div>" +
					"</div>";

	private static final String CONTENT_HTML =
			"<div class=\"dictionaryBlock\">" +
					"<div class=\"searchedWord\">%s</div>" +
					"<div class=\"dictionaryContent\">%s</div>" +
					"<div class=\"dictionaryName\">%s</div>" +
					"</div>";

	private static final String CONTENT_WELCOME_HTML =
			"<div class=\"welcomeBlock\">" +
					"%s" +
					"</div>";

	private static final StringBuilder CONTENT_MAKER = new StringBuilder();

	private ResultTextMaker() {}

	public static void appendContent(final String searchedWord, final String content, final String dictionaryName) {
		CONTENT_MAKER.append(String.format(CONTENT_HTML, searchedWord, content, dictionaryName));
	}

	public static String getResultHTML() {
		return String.format(RESULT_HTML, CONTENT_MAKER.toString());
	}

	public static String getNoResultHTML( final String searchedWord, final String noResultStr) {
		final String contentFailureHTML = String.format(CONTENT_FAILURE_HTML, searchedWord, noResultStr);
		return String.format(RESULT_HTML, contentFailureHTML);
	}

	public static String getWelcomeHTML(final String welcomeStr) {
		final String contentWelcomeHTML = String.format(CONTENT_WELCOME_HTML, welcomeStr);
		return String.format(RESULT_HTML, contentWelcomeHTML);
	}

	public static void resetMiddleBlock() {
		CONTENT_MAKER.setLength(0);
	}
}
