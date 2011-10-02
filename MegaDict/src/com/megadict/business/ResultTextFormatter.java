package com.megadict.business;

public final class ResultTextFormatter {
	/**
	 * The elemnents in DICT dictionary format.
	 */
	public static final String ENTRY = "@";
	public static final String WORDKIND = "*";
	public static final String MEANING = "-";
	public static final String IDIOM = "!";
	public static final String EXAMPLE_BEGIN = "=";
	public static final String EXAMPLE_SEPARATOR = "+";
	public static final String LINK = "$";
	public static final String RELATED = "#";

	/**
	 * The CSS classes correspoding to the above elements.
	 */
	public static final String ENTRY_CLASS = "entry";
	public static final String WORDKIND_CLASS = "wordKind";
	public static final String MEANING_CLASS = "meaning";
	public static final String IDIOM_CLASS = "idiom";
	public static final String EXAMPLE_BEGIN_CLASS = "exampleBegin";
	public static final String EXAMPLE_SEPARATOR_CLASS = "exampleSeparator";
	public static final String LINK_CLASS = "link";
	public static final String RELATED_CLASS = "related";

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String START_DIV = "<div class=\"%s\">";
	private static final String END_DIV = "</div>";
	private static final String START_SPAN = "<span class=\"%s\">";
	private static final String END_SPAN = "</span>";

	private ResultTextFormatter() {}

	public static String format(final String content) {
		final StringBuilder builder = new StringBuilder();
		final String []lines = content.split("\n");

		for(final String line : lines) {
			String className = "";

			// Trim and escape tag characters.
			String changedLine = line.trim()
					.replace("<", "&lt;")
					.replace(">", "&gt;");

			if(line.startsWith(ENTRY)) {
				className = ENTRY_CLASS;
			} else if(line.startsWith(WORDKIND)) {
				className = WORDKIND_CLASS;
			} else if(line.startsWith(MEANING)) {
				className = MEANING_CLASS;
			} else if(line.startsWith(EXAMPLE_BEGIN)) {
				className = EXAMPLE_BEGIN_CLASS;
				final int index = line.indexOf(EXAMPLE_SEPARATOR);
				if(index != -1) {
					changedLine = line.substring(0, index) +
							String.format(START_SPAN, EXAMPLE_SEPARATOR_CLASS) +
							line.substring(index) + END_SPAN;
				}
			} else if(line.startsWith(IDIOM)) {
				className = IDIOM_CLASS;
			}

			final String formattedLine = String.format(START_DIV, className) + changedLine + END_DIV;
			builder.append(formattedLine);
		}
		return builder.toString();
	}
}
