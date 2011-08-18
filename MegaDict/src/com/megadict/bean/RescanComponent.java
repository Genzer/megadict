package com.megadict.bean;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;

public class RescanComponent {
	public Context context;
	public ProgressDialog progressDialog;
	public Cursor cursor;

	public RescanComponent(final Context context, final ProgressDialog progressDialog, final Cursor cursor) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.cursor = cursor;
	}
}
