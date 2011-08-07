package com.megadict.task;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.R;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.business.DictionaryScanner;
import com.megadict.business.ResultTextMaker;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.model.UsedDictionary;
import com.megadict.task.base.BaseScanTask;

public class ScanStorageAllTask extends BaseScanTask {
	private final ScanStorageComponent scanStorageComponent;
	private final Activity activity;
	private final SQLiteDatabase database;
	private final ModelMap models;

	public ScanStorageAllTask(final ModelMap models, final Activity activity, final SQLiteDatabase database, final ScanStorageComponent scanStorageComponent) {
		super();
		this.models = models;
		this.activity = activity;
		this.database = database;
		this.scanStorageComponent = scanStorageComponent;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		// Remove old dicts.
		models.clear();
		// Read from database.
		final Cursor cursor = ChosenModel.selectChosenDictionaryIDsAndPaths(database);
		activity.startManagingCursor(cursor);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			try {
				final String dictPath = cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN));
				final DictionaryInformation info = DictionaryInformation.newInstance(dictPath);
				// Create necessary files.
				final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
				final DictionaryFile dictionaryFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
				// Create model.
				final Dictionary model = UsedDictionary.newInstance(indexFile, dictionaryFile);
				// Get dictionary ID.
				final int dictID = (int)cursor.getLong(cursor.getColumnIndex(ChosenModel.ID_COLUMN));
				// Store model.
				models.put(dictID, model);
			} catch (final IndexFileNotFoundException e) {
				DictionaryScanner.log(e.getMessage());
			} catch (final DataFileNotFoundException e) {
				DictionaryScanner.log(e.getMessage());
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		setStartPage();
	}

	private void setStartPage() {
		final int dictCount = models.size();
		final String welcomeStr = (dictCount > 1 ? scanStorageComponent.context.getString(R.string.usingDictionaryPlural, dictCount) : scanStorageComponent.context.getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = scanStorageComponent.resultTextMaker.getWelcomeHTML(welcomeStr);
		scanStorageComponent.resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}
}
