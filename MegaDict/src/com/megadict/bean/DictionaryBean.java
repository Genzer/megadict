package com.megadict.bean;

import java.util.Date;

public class DictionaryBean {
	private final int id;
	private final String name;
	private final String sourceLanguage;
	private final String targetLanguage;
	private final String description;
	private final Date createDate;

	public DictionaryBean(final int id, final String name, final String sourceLanguage, final String targetLanguage, final String description, final Date createDate) {
		this.id = id;
		this.name = name;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.description = description;
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreateDate() {
		return createDate;
	}

}
