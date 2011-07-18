package com.megadict.format.dict;

enum MetaDataEntry {

    FULL_DESCRIPTION("00-database-info"),
    SHORT_NAME("00-database-short"),
    URL("00-database-url");

    MetaDataEntry(String tagName) {
        this.tagName = tagName;
    }

    public String tagName() {
        return this.tagName;
    }

    private final String tagName;
}
