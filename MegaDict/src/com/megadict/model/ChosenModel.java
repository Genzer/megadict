package com.megadict.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class ChosenModel {
	public static final String TABLE_NAME = "chosen";
	public static final String ID_COLUMN = "_id";
	public static final String DICTIONARY_NAME_COLUMN = "dictionary_name";
	public static final String INDEX_PATH_COLUMN = "index_path";
	public static final String DICT_PATH_COLUMN = "dict_path";
	public static final String ENABLED_COLUMN = "enabled";
	public static final String CREATE_TABLE = "CREATE TABLE "
		+ TABLE_NAME + "("
		+ ID_COLUMN + " INTEGER PRIMARY KEY, "
		+ DICTIONARY_NAME_COLUMN + " TEXT, "
		+ INDEX_PATH_COLUMN + " TEXT, "
		+ DICT_PATH_COLUMN + " TEXT, "
		+ ENABLED_COLUMN + " INTEGER);";
	public static final String DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static Cursor selectChosenDictionaries(final SQLiteDatabase database)
	{
		final Cursor cursor =
			database.query(
					ChosenModel.TABLE_NAME,
					new String[] { ChosenModel.INDEX_PATH_COLUMN, ChosenModel.DICT_PATH_COLUMN },
					ChosenModel.ENABLED_COLUMN + " = ?",
					new String[]{"1"},
					null,
					null,
					ChosenModel.DICTIONARY_NAME_COLUMN);
		return cursor;
	}

	public static Cursor selectAll(final SQLiteDatabase database)
	{
		final Cursor cursor =
			database.query(
					ChosenModel.TABLE_NAME,
					new String[] { ChosenModel.INDEX_PATH_COLUMN, ChosenModel.DICT_PATH_COLUMN },
					null,
					null,
					null,
					null,
					ChosenModel.DICTIONARY_NAME_COLUMN);
		return cursor;
	}


}
