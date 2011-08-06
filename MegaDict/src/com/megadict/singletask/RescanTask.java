package com.megadict.singletask;

import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.business.DictionaryScanner;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.singletask.base.BaseScanTask;

public class RescanTask extends BaseScanTask {
	private final ProgressDialog progressDialog;
	private final SQLiteDatabase database;
	private final Cursor cursor;

	public RescanTask(final DictionaryInformation info, final List<Dictionary> dictionaryModels, final ProgressDialog progressDialog, final SQLiteDatabase database, final Cursor cursor) {
		super(info, dictionaryModels);
		this.progressDialog = progressDialog;
		this.database = database;
		this.cursor = cursor;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(final Void... params) {
		// Create necessary files.
		final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
		final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
		// Create model and add it to list.
		final Dictionary model = new DICTDictionary(indexFile, dictFile);
		//final Dictionary model = new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();
		dictionaryModels.add(model);

		// Insert dictionary infos to database.
		final ContentValues value = new ContentValues();
		value.put(ChosenModel.DICTIONARY_NAME_COLUMN, model.getName());
		value.put(ChosenModel.DICTIONARY_PATH_COLUMN, info.getParentFile().getAbsolutePath());
		value.put(ChosenModel.ENABLED_COLUMN, 0);
		database.insert(ChosenModel.TABLE_NAME, null, value);
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		if(DictionaryScanner.didAllTasksFinish()) {
			// Requery the cursor to update list view.
			cursor.requery();
			// Close progress dialog
			progressDialog.dismiss();
		}
	}

}
