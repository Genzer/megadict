package com.megadict.business;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.megadict.bean.DictionaryBean;
import com.megadict.exception.DocumentParserException;
import com.megadict.exception.InvalidConfigurationFileException;
import com.megadict.parser.DocumentParser;

public class ConfigurationReader {
	public DictionaryBean readConfigurationFile(final File file) throws InvalidConfigurationFileException {
		try {
			final DocumentParser parser = new DocumentParser();
			final Document doc = parser.parse(file);

			final Element root = doc.getDocumentElement();
			final NodeList children = root.getChildNodes();

			final int id = Integer.parseInt(getDataFromNodeList("id", children));
			final String name = getDataFromNodeList("name", children);
			final String sourceLang = getDataFromNodeList("sourceLanguage", children);
			final String targetLang = getDataFromNodeList("targetLanguage", children);
			final String description = getDataFromNodeList("description", children);

			// Get string-formed date and parse it to Date object.
			final String createDateString = getDataFromNodeList("createDate", children);
			final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			final Date createDate = dateFormatter.parse(createDateString);

			// Check all attributes. If any of them is null, throw exception.
			if (isAnyOfDictAttributesNull(name, sourceLang, targetLang, description, createDate)) {
				throw new InvalidConfigurationFileException();
			}

			return new DictionaryBean(id, name, sourceLang, targetLang, description, createDate);
		} catch (final ParseException e) {
			throw new InvalidConfigurationFileException();
		} catch (final DocumentParserException e) {
			throw new InvalidConfigurationFileException();
		}
	}

	private boolean isAnyOfDictAttributesNull(final String name, final String sourceLang, final String targetLang, final String description, final Date createDate) {
		return (name == null || sourceLang == null || targetLang == null || description == null || createDate == null);
	}

	private String getDataFromNodeList(final String attributeName, final NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node child = nodeList.item(i);
			if (child instanceof Element) {
				final Element childElement = (Element) child;
				final String attrib = childElement.getAttribute("key");
				final Text textNode = (Text) childElement.getFirstChild();
				final String data = textNode != null ? textNode.getData() : "";
				if (attrib.equals(attributeName)) {
					return data;
				}
			}
		}
		return "";
	}
}
