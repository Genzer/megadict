package com.megadict.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class ChosenModel {
	public static final String TABLE_NAME = "chosen";
	public static final String ID_COLUMN = "_id";
	public static final String DICTIONARY_NAME_COLUMN = "dictionary_name";
	public static final String DICTIONARY_PATH_COLUMN = "path";
	public static final String ENABLED_COLUMN = "enabled";
	public static final String CREATE_TABLE = "CREATE TABLE "
		+ TABLE_NAME + "("
		+ ID_COLUMN + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "
		+ DICTIONARY_NAME_COLUMN + " TEXT, "
		+ DICTIONARY_PATH_COLUMN + " TEXT, "
		+ ENABLED_COLUMN + " INTEGER);";
	public static final String DROP_TABLE =
		"DROP TABLE IF EXISTS " + TABLE_NAME;

	public static Cursor selectChosenDictionaryPaths(final SQLiteDatabase database)
	{
		final Cursor cursor =
			database.query(
					ChosenModel.TABLE_NAME,
					new String[] { ChosenModel.DICTIONARY_PATH_COLUMN },
					ChosenModel.ENABLED_COLUMN + " = ?",
					new String[]{"1"},
					null,
					null,
					ChosenModel.DICTIONARY_NAME_COLUMN);
		return cursor;
	}

	public static Cursor selectChosenDictionaryIDsAndPaths(final SQLiteDatabase database)
	{
		final Cursor cursor =
			database.query(
					ChosenModel.TABLE_NAME,
					new String[] { ChosenModel.ID_COLUMN, ChosenModel.DICTIONARY_PATH_COLUMN },
					ChosenModel.ENABLED_COLUMN + " = ?",
					new String[]{"1"},
					null,
					null,
					ChosenModel.DICTIONARY_NAME_COLUMN);
		return cursor;
	}

	public static Cursor selectChosenDictionaryIDs(final SQLiteDatabase database)
	{
		final Cursor cursor =
			database.query(
					ChosenModel.TABLE_NAME,
					new String[] { ChosenModel.ID_COLUMN },
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
					new String[] { ChosenModel.DICTIONARY_PATH_COLUMN },
					null,
					null,
					null,
					null,
					ChosenModel.DICTIONARY_NAME_COLUMN);
		return cursor;
	}


}
