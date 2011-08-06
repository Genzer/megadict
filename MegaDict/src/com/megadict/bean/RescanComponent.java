package com.megadict.bean;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RescanComponent {
	public ProgressDialog progressDialog;
	public SQLiteDatabase database;
	public Cursor cursor;

	public RescanComponent(final ProgressDialog progressDialog, final SQLiteDatabase database, final Cursor cursor) {
		this.progressDialog = progressDialog;
		this.database = database;
		this.cursor = cursor;
	}
}
