package com.megadict.business;

import java.io.IOException;

import android.content.res.AssetManager;

import com.megadict.exception.ResourceNotFoundException;

public class ResultTextMaker {
	public static final String ASSET_URL = "file:///android_asset/";
	private final AssetManager assetManager;

	private final String middleWelcomeBlock = "<div class=\"welcomeBlock\">";
	private final String rightWelcomeBlock = "</div></body></html>";

	private final String middleNoDictBlock = "<div class=\"noDictionaryBlock\">";
	private final String rightNoDictBlock = "</div></body></html>";

	private final StringBuilder leftBlock = new StringBuilder();
	private final StringBuffer middleBlock = new StringBuffer();
	private final String rightBlock = "</body></html>";

	public ResultTextMaker(final AssetManager assetManager) {
		this.assetManager = assetManager;
		initLefltBlock();
	}

	private void initLefltBlock() {
		try {
			leftBlock.append("<html>");
			// Append stylesheets.
			final String []cssNames = assetManager.list("css");
			leftBlock.append("<head>");
			for(final String cssName : cssNames) {
				leftBlock.append("<link href=\"css/" + cssName + "\" rel=\"stylesheet\" type=\"text/css\" />");

			}
			// Append JQuery.
			final String []scriptNames = assetManager.list("scripts");
			for(final String scriptName : scriptNames) {
				leftBlock.append("<script src=\"scripts/" + scriptName + "\" type=\"text/javascript\"></script>");
			}
			leftBlock.append("</head><body>");
		} catch (final IOException e) {
			// Because it's a programming exception, we don't need to catch it.
			/// Just throw unchecked exception.
			throw new ResourceNotFoundException(e.getCause());
		}
	}

	public void appendContent(final String content, final String dictionaryName) {
		final String formattedContent = content.trim().replace("\n", "<br/>");
		middleBlock.append("<div class=\"dictionaryBlock\"><div class=\"dictionaryContent\">" + formattedContent + "</div>" +
				"<div class=\"dictionaryName\">" + dictionaryName + "</div></div>");
	}

	public String getResultHTML() {
		return leftBlock.toString() + middleBlock.toString() + rightBlock;
	}

	public String getWelcomeHTML(final String welcomeStr) {
		return leftBlock.toString() + middleWelcomeBlock + welcomeStr + rightWelcomeBlock;
	}

	public String getNoDictionaryHTML(final String noDictContent) {
		return leftBlock.toString() + middleNoDictBlock + noDictContent + rightNoDictBlock;
	}

	public void resetMiddleBlock() {
		middleBlock.setLength(0);
	}
}
