package com.megadict.business.scanning;

import android.app.Activity;
import android.database.Cursor;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
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

public class ScanStorageAllTask extends BaseScanTask {
	private final DictionaryScanner scanner;
	private final DictionaryComponent dictionaryComponent;
	private final Activity activity;
	private final ModelMap models;

	public ScanStorageAllTask(final DictionaryScanner scanner, final ModelMap models, final Activity activity,
			final DictionaryComponent dictionaryComponent) {
		super();
		this.scanner = scanner;
		this.models = models;
		this.activity = activity;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		// Remove old dicts.
		models.clear();
		// Read from database.
		final Cursor cursor = ChosenModel.selectChosenDictionaryIDsAndPaths(dictionaryComponent.getDatabase());
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
		scanner.dictionaryModelsChanged();
		setStartPage();
	}

	private void setStartPage() {
		final int dictCount = models.size();
		final String welcomeStr = (dictCount > 1 ?
				dictionaryComponent.getContext().getString(R.string.usingDictionaryPlural, dictCount) :
					dictionaryComponent.getContext().getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = dictionaryComponent.getResultTextMaker().getWelcomeHTML(welcomeStr);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}
}
