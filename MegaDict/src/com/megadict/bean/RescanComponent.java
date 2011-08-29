package com.megadict.bean;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;

/**
 * RescanComponent is a bean help rescan storage. It provides components for showing ProgressDialog,
 * requerying cursor, getting database from context.
 * 
 * @author HIEUGIOI
 */
public class RescanComponent {
	private final Context context;
	private final ProgressDialog progressDialog;
	private final Cursor cursor;

	public RescanComponent(final Context context, final ProgressDialog progressDialog, final Cursor cursor) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.cursor = cursor;
	}

	public Context getContext() {
		return context;
	}

	public Cursor getCursor() {
		return cursor;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialogMessage(final int resID) {
		progressDialog.setMessage(context.getString(resID));
	}
}
