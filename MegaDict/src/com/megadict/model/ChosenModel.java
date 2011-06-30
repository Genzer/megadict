package com.megadict.model;


public class ChosenModel {
	public static final String TABLE_NAME = "chosen";
	public static final String ID_COLUMN = "_id";
	public static final String DICTIONARY_NAME_COLUMN = "dictionary_name";
	public static final String ENABLED_COLUMN = "enabled";
	public static final String CREATE_TABLE = "CREATE TABLE "
		+ TABLE_NAME + "(" + ID_COLUMN + " INTEGER PRIMARY KEY, "
		+ DICTIONARY_NAME_COLUMN + " TEXT, " + ENABLED_COLUMN
		+ " INTEGER);";
	public static final String DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;
}
