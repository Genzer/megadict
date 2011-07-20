package com.megadict.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;

public class ResultTextMaker {
	public static final String ASSET_URL = "file:///android_asset/";
	private final AssetManager assetManager;
	private final List<String> logger = new ArrayList<String>();
	private final String headTag;

	public ResultTextMaker(final AssetManager assetManager) {
		this.assetManager = assetManager;
		headTag = createHeadTag();
	}

	private String createHeadTag() {
		final StringBuilder headTagContent = new StringBuilder();
		try {
			// Append stylesheets.
			final String []cssNames = assetManager.list("css");
			headTagContent.append("<head>");
			for(final String cssName : cssNames) {
				headTagContent.append("<link href=\"css/" + cssName + "\" rel=\"stylesheet\" type=\"text/css\" />");
			}
			// Append JQuery.
			final String []scriptNames = assetManager.list("scripts");
			for(final String scriptName : scriptNames) {
				headTagContent.append("<script src=\"scripts/" + scriptName + "\" type=\"text/javascript\"></script>");
			}
			headTagContent.append("</head>");
		} catch (final IOException e) {
			logger.add(e.getMessage());
		}
		return headTagContent.toString();
	}

	public String getWelcomeHTML(final List<String> dictionaryNames) {
		final int dictCount = dictionaryNames.size();
		final String welcomeStr = "<html>" + headTag + "<body>You are using " +
		dictCount + (dictCount > 1 ? " dictionaries" : " dictionary") + "</body></html>";
		return welcomeStr;
	}

	public String getResultHTML(final List<String> contents, final List<String> dictionaryNames) {
		final int contentSize = contents.size(), dictionaryNameSize = dictionaryNames.size();
		if(contentSize != dictionaryNameSize) {
			throw new IllegalArgumentException("content size must equal to dictionaryNames size.");
		}

		final StringBuilder resultText = new StringBuilder();
		resultText.append("<html>" + headTag + "<body>");

		// Create dictionary blocks.
		for(int i = 0; i < contentSize; ++i) {
			final String formattedContent = contents.get(i).replace("\n", "<br/>");
			resultText.append("<div class=\"dictionaryBlock\"><div class=\"dictionaryContent\">" + formattedContent + "</div>" +
					"<div class=\"dictionaryName\">" + dictionaryNames.get(i) + "</div></div>");
		}
		resultText.append("</body></html>");
		return resultText.toString();
	}

	public String getNoDictionaryHTML(final String content) {
		final StringBuilder resultText = new StringBuilder();
		resultText.append("<html>" + headTag + "<body>");
		resultText.append("<div class=\"dictionaryBlock\"><div class=\"noDictionaryContent\">" + content + "</div>" +
		"</div></body></html>");
		return resultText.toString();
	}
}
