package com.megadict.bean;

import com.megadict.model.ChosenModel;

public class DictionaryBean {
	private final int id;
	private final String name;
	private final String type;
	private final String path;
	private final int enabled;

	public static class Builder {
		// Required.
		private final int id;

		// Optional.
		private String name = "No name";
		private String path = "No path";
		private String type = ChosenModel.LOCAL_DICTIONARY;
		private int enabled = 0;

		public Builder(final int id) {
			this.id = id;
		}

		public Builder name(final String name) {
			this.name = name;
			return this;
		}

		public Builder path(final String path) {
			this.path = path;
			return this;
		}

		public Builder type(final String type) {
			this.type = type;
			return this;
		}

		public Builder enabled(final int enabled) {
			this.enabled = enabled;
			return this;
		}

		public DictionaryBean build() {
			return new DictionaryBean(this);
		}
	}

	public DictionaryBean(final Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.path = builder.path;
		this.type = builder.type;
		this.enabled = builder.enabled;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public int getEnabled() {
		return enabled;
	}

}
