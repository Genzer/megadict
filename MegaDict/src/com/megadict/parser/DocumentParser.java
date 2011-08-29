package com.megadict.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.megadict.exception.DocumentParserException;

public class DocumentParser {

	/**
	 * @param file File needs to be parsed.
	 * @return Document is parsed.
	 * @throws DocumentParserException if the document cannot be created.
	 */
	public Document parse(final File file) throws DocumentParserException {
		final DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(file);
		} catch (final ParserConfigurationException e) {
			throw new DocumentParserException();
		} catch (final SAXException e) {
			throw new DocumentParserException();
		} catch (final IOException e) {
			throw new DocumentParserException();
		}
	}
}
