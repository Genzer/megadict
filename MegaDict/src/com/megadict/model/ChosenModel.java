package com.megadict.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public final class ChosenModel {
	public static final String LOCAL_DICTIONARY = "local";
	public static final String WIKI_DICTIONARY = "wiki";

	public static final String TABLE_NAME = "chosen";
	public static final String ID_COLUMN = "_id";
	public static final String DICTIONARY_NAME_COLUMN = "dictionary_name";
	public static final String DICTIONARY_PATH_COLUMN = "path";
	public static final String DICTIONARY_TYPE_COLUMN = "type";
	public static final String ENABLED_COLUMN = "enabled";
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ TABLE_NAME + "("
			+ ID_COLUMN + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "
			+ DICTIONARY_NAME_COLUMN + " TEXT, "
			+ DICTIONARY_PATH_COLUMN + " TEXT, "
			+ DICTIONARY_TYPE_COLUMN + " TEXT, "
			+ ENABLED_COLUMN + " INTEGER);";
	public static final String DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;

	private ChosenModel() {}

	public static int insertDictionary(final SQLiteDatabase database, final String dictionaryName, final String dictionaryPath, final String dictionaryType, final int enabled) {
		final ContentValues value = new ContentValues();
		value.put(ChosenModel.DICTIONARY_NAME_COLUMN, dictionaryName);
		value.put(ChosenModel.DICTIONARY_PATH_COLUMN, dictionaryPath);
		value.put(ChosenModel.DICTIONARY_TYPE_COLUMN, dictionaryType);
		value.put(ChosenModel.ENABLED_COLUMN, enabled);
		return (int)database.insert(ChosenModel.TABLE_NAME, null, value);
	}

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
						new String[] { ChosenModel.ID_COLUMN, ChosenModel.DICTIONARY_PATH_COLUMN, ChosenModel.DICTIONARY_TYPE_COLUMN },
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

	public static Cursor selectChosenDictionaryIDsPathsAndTypes(final SQLiteDatabase database)
	{
		final Cursor cursor =
				database.query(
						ChosenModel.TABLE_NAME,
						new String[] { ChosenModel.ID_COLUMN, ChosenModel.DICTIONARY_PATH_COLUMN, ChosenModel.DICTIONARY_TYPE_COLUMN },
						ChosenModel.ENABLED_COLUMN + " = ?",
						new String[]{"1"},
						null,
						null,
						ChosenModel.DICTIONARY_NAME_COLUMN);
		return cursor;
	}


}
