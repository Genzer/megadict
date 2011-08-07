package com.megadict.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.model.ChosenModel;
import com.megadict.task.base.BaseScanTask;

public class UpdateModelTask extends BaseScanTask {
	private SQLiteDatabase database;

	public UpdateModelTask() {

	}

	@Override
	protected Void doInBackground(final Void... params) {
		final Cursor cursor = ChosenModel.selectChosenDictionaryIDs(database);


		return null;
	}
}
