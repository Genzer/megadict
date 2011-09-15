package com.megadict.bean;

import android.app.ProgressDialog;
import android.database.Cursor;

/**
 * RescanComponent is a bean helps rescan storage. It provides components for showing ProgressDialog,
 * requerying cursor.
 * 
 * @author HIEUGIOI
 */
public class ManageComponent {
	private final ProgressDialog progressDialog;
	private final Cursor cursor;

	public ManageComponent(final ProgressDialog progressDialog, final Cursor cursor) {
		this.progressDialog = progressDialog;
		this.cursor = cursor;
	}

	public Cursor getCursor() {
		return cursor;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialogMessage(final String message) {
		progressDialog.setMessage(message);
	}
}
