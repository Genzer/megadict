package com.megadict.business;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.megadict.bean.DictionaryBean;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.DocumentParserException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.exception.InvalidConfigurationFileException;
import com.megadict.model.DictionaryInformation;
import com.megadict.parser.DocumentParser;

public class ExternalReader {
	private final List<DictionaryBean> beans = new ArrayList<DictionaryBean>();
	private final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
	private final List<String> logger = new ArrayList<String>();

	public static final String INVALID_CONFIGURATION_FILE = "Invalid configuration file.";
	public static final String NO_DICTIONARY = "There is no dictionary.";
	public static final String CONFIGURATION_FILE_NOT_FOUND = "Configuration file not found.";
	public static final String INDEX_FILE_NOT_FOUND = "Index file not found.";
	public static final String DATA_FILE_NOT_FOUND = "Data file not found.";

	public List<DictionaryBean> getBeans() {
		return beans;
	}

	public List<DictionaryInformation> getInfos() {
		return infos;
	}

	public List<String> getLogger() {
		return logger;
	}

	public ExternalReader(final File externalFile) {
		init(externalFile);
	}

	private void init(final File externalFile) {
		final File files[] = externalFile.listFiles();
		if (files == null) throw new IllegalArgumentException("External storage is not a valid directory.");

		DictionaryInformation info = null;
		DictionaryBean bean = null;

		for (final File file : files) {
			if (file.isDirectory()) {
				final File configurationFile = getConfigurationFile(file);
				final String locationInfo = " Location: " + file.getAbsolutePath();

				// If configuration file is not found, just silently ignore it.
				if(configurationFile == null) {
					logger.add(CONFIGURATION_FILE_NOT_FOUND + locationInfo);
					continue;
				}

				try {
					bean = createDictionaryBean(configurationFile);
					info = createDictionaryInformation(bean, file);
				} catch (final IndexFileNotFoundException e) {
					logger.add(INDEX_FILE_NOT_FOUND + locationInfo);
				} catch (final DataFileNotFoundException e) {
					logger.add(DATA_FILE_NOT_FOUND + locationInfo);
				} catch (final InvalidConfigurationFileException e) {
					logger.add(INVALID_CONFIGURATION_FILE + locationInfo);
				}

				// If index file or data file are not found, just silently ignore it.
				if(bean == null || info == null) continue;

				// Store bean and info for later use.
				beans.add(bean);
				infos.add(info);
			}
		}
	}

	private File getConfigurationFile(final File parentDirectory) {
		final File files[] = parentDirectory.listFiles();
		File configurationFile = null;
		for (final File file : files) {
			if (file.getName().equals("conf.xml")) {
				configurationFile = file;
			}
		}
		return configurationFile;
	}

	private DictionaryInformation createDictionaryInformation(final DictionaryBean bean, final File file) throws IndexFileNotFoundException, DataFileNotFoundException {
		return new DictionaryInformation(bean, file);
	}

	private DictionaryBean createDictionaryBean(final File file) throws InvalidConfigurationFileException {
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
		} catch (final NumberFormatException e) {
			throw new InvalidConfigurationFileException();
		}
	}

	private boolean isAnyOfDictAttributesNull(final String name, final String sourceLang, final String targetLang, final String description, final Date createDate) {
		return (name == null || sourceLang == null || targetLang == null || description == null || createDate == null);
	}

	private String getDataFromNodeList(final String attributeName, final NodeList nodeList) {
		String data = "";
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node child = nodeList.item(i);
			if (child instanceof Element) {
				final Element childElement = (Element) child;
				final String attrib = childElement.getAttribute("key");
				final Text textNode = (Text) childElement.getFirstChild();
				data = textNode != null ? textNode.getData() : "";
				if (attrib.equals(attributeName)) break;
			}
		}
		return data;
	}
}
