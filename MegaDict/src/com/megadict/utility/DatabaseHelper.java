package com.megadict.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.megadict.model.ChosenModel;

public final class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "dictionary";
	public static final int DATABASE_VERSION = 2;
	private static DatabaseHelper helper;
	private static SQLiteDatabase database;

	private DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(ChosenModel.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		// Logs that the database is being upgraded
		Log.w("Something", "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");

		// Kills the table and existing data
		db.execSQL(ChosenModel.DROP_TABLE);

		// Recreates the database with a new version
		onCreate(db);
	}

	public static DatabaseHelper getInstance(final Context context) {
		if (helper == null) {
			helper = new DatabaseHelper(context);
		}
		return helper;
	}

	public static SQLiteDatabase getDatabase(final Context context) {
		helper = getInstance(context);
		if (database == null) {
			database = helper.getWritableDatabase();
		}
		return database;
	}
}
