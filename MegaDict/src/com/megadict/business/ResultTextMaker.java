package com.megadict.business;

import java.io.IOException;

import android.content.res.AssetManager;

import com.megadict.exception.ResourceNotFoundException;

/**
 * This class is not threadsafe.
 * @author HIEUGIOI
 *
 */
public class ResultTextMaker {
	public static final String ASSET_URL = "file:///android_asset/";
	private final AssetManager assetManager;

	private static final String MIDDLE_WELCOME_BLOCK =
			"<div class=\"welcomeBlock\">";
	private static final String RIGHT_WELCOME_BLOCK = "</div></body></html>";

	private static final String MIDDLE_NO_DICT_BLOCK =
			"<div class=\"noDictionaryBlock\">";
	private static final String RIGHT_NO_DICT_BLOCK = "</div></body></html>";

	private static final StringBuilder LEFT_BLOCK = new StringBuilder();
	private static final StringBuilder MIDDLE_BLOCK = new StringBuilder();
	private static final String RIGHT_BLOCK = "</body></html>";

	public ResultTextMaker(final AssetManager assetManager) {
		this.assetManager = assetManager;
		initLefltBlock();
	}

	private void initLefltBlock() {
		try {
			LEFT_BLOCK.append("<html>");
			// Append CSS.
			final String[] cssNames = assetManager.list("css");
			LEFT_BLOCK.append("<head>");
			for (final String cssName : cssNames) {
				LEFT_BLOCK.append("<link href=\"css/" + cssName
						+ "\" rel=\"stylesheet\" type=\"text/css\" />");
			}
			// Append JQuery.
			final String[] scriptNames = assetManager.list("scripts");
			for (final String scriptName : scriptNames) {
				LEFT_BLOCK.append("<script src=\"scripts/" + scriptName
						+ "\" type=\"text/javascript\"></script>");
			}
			LEFT_BLOCK.append("</head><body>");
		} catch (final IOException e) {
			throw new ResourceNotFoundException(e);
		}
	}

	public void appendContent(final String searchedWord, final String content, final String dictionaryName) {
		final String formattedContent = content.trim().replace("\n", "<br/>");
		MIDDLE_BLOCK.append("<div class=\"dictionaryBlock\">"
				+ "<div class=\"searchedWord\">" + searchedWord + "</div>"
				+ "<div class=\"dictionaryContent\">" + formattedContent
				+ "</div>" + "<div class=\"dictionaryName\">" + dictionaryName
				+ "</div></div>");
	}

	public String getResultHTML() {
		return LEFT_BLOCK.toString() + MIDDLE_BLOCK.toString() + RIGHT_BLOCK;
	}

	public String getWelcomeHTML(final String welcomeStr) {
		return LEFT_BLOCK.toString() + MIDDLE_WELCOME_BLOCK + welcomeStr
				+ RIGHT_WELCOME_BLOCK;
	}

	public String getNoDictionaryHTML(final String noDictContent) {
		return LEFT_BLOCK.toString() + MIDDLE_NO_DICT_BLOCK + noDictContent
				+ RIGHT_NO_DICT_BLOCK;
	}

	public void resetMiddleBlock() {
		MIDDLE_BLOCK.setLength(0);
	}
}
