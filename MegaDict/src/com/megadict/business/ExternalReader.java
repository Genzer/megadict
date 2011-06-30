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
import com.megadict.exception.ConfigurationFileNotFoundException;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.DictionaryNotFoundException;
import com.megadict.exception.DocumentParserException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.exception.InvalidConfigurationFileException;
import com.megadict.model.DictionaryInformation;
import com.megadict.parser.DocumentParser;

public class ExternalReader {
	/**
	 * Get dictionary information list.
	 * 
	 * @param externalFile External storage path.
	 * @return List of dictionary information
	 * @throws InvalidConfigurationFileException if configuration file invalid.
	 * @throws DictionaryNotFoundException if no dictionary found.
	 * @throws ConfigurationFileNotFoundException if configuration file not found.
	 * @throws DataFileNotFoundException if data file not found.
	 * @throws IndexFileNotFoundException if index file not found.
	 */
	public static List<DictionaryInformation> getDictionaryInformationList(final File externalFile) throws InvalidConfigurationFileException, ConfigurationFileNotFoundException, IndexFileNotFoundException, DataFileNotFoundException, DictionaryNotFoundException {
		final List<DictionaryInformation> infoList = new ArrayList<DictionaryInformation>();
		final File files[] = externalFile.listFiles();
		if (files == null) throw new InvalidConfigurationFileException();
		if (files.length == 0) throw new DictionaryNotFoundException();

		for (final File file : files) {
			if (file.isDirectory()) {
				final File configurationFile = getConfigurationFile(file);
				final DictionaryBean bean = getDictionaryBean(configurationFile);
				final DictionaryInformation info = new DictionaryInformation(bean, file);
				infoList.add(info);
			}
		}
		return infoList;
	}

	/**
	 * Get dictionary bean list.
	 * 
	 * @param externalFile External storage path.
	 * @return List of dictionary beans
	 * @throws InvalidConfigurationFileException
	 * @throws DictionaryNotFoundException
	 * @throws ConfigurationFileNotFoundException
	 */
	public static List<DictionaryBean> getDictionaryBeanList(final File externalFile) throws InvalidConfigurationFileException, DictionaryNotFoundException, ConfigurationFileNotFoundException {
		final List<DictionaryBean> beanList = new ArrayList<DictionaryBean>();
		final File files[] = externalFile.listFiles();
		if (files == null) throw new InvalidConfigurationFileException();
		if (files.length == 0) throw new DictionaryNotFoundException();

		for (final File file : files) {
			if (file.isDirectory()) {
				final File configurationFile = getConfigurationFile(file);
				final DictionaryBean bean = getDictionaryBean(configurationFile);
				beanList.add(bean);
			}
		}
		return beanList;
	}

	/**
	 * Get dictionary name list.
	 * 
	 * @param externalFile
	 * @return List of dictionary names
	 * @throws InvalidConfigurationFileException
	 * @throws DictionaryNotFoundException
	 * @throws ConfigurationFileNotFoundException
	 */
	public static List<String> getDictionaryNameList(final File externalFile) throws InvalidConfigurationFileException, DictionaryNotFoundException, ConfigurationFileNotFoundException {
		final List<String> nameList = new ArrayList<String>();
		final File files[] = externalFile.listFiles();
		if (files == null) throw new InvalidConfigurationFileException();
		if (files.length == 0) throw new DictionaryNotFoundException();

		for (final File file : files) {
			if (file.isDirectory()) {
				final File configurationFile = getConfigurationFile(file);
				final String name = getDictionaryName(configurationFile);
				nameList.add(name);
			}
		}
		return nameList;
	}

	private static File getConfigurationFile(final File parentDirectory) throws ConfigurationFileNotFoundException {
		final File files[] = parentDirectory.listFiles();
		for (final File file : files) {
			if (file.getName().equals("conf.xml")) {
				return file;
			}
		}
		throw new ConfigurationFileNotFoundException();
	}

	private static String getDictionaryName(final File file) throws InvalidConfigurationFileException {
		try {
			final DocumentParser parser = new DocumentParser();
			final Document doc = parser.parse(file);

			final Element root = doc.getDocumentElement();
			final NodeList children = root.getChildNodes();
			final String name = getDataFromNodeList("name", children);

			if (name == null) throw new InvalidConfigurationFileException();
			return name;
		} catch (final DocumentParserException e) {
			throw new InvalidConfigurationFileException();
		}
	}

	private static DictionaryBean getDictionaryBean(final File file) throws InvalidConfigurationFileException {
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

	private static boolean isAnyOfDictAttributesNull(final String name, final String sourceLang, final String targetLang, final String description, final Date createDate) {
		return (name == null || sourceLang == null || targetLang == null || description == null || createDate == null);
	}

	private static String getDataFromNodeList(final String attributeName, final NodeList nodeList) {
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
