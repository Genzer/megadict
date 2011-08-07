package com.megadict.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class UpdateModelTask extends BaseScanTask {
	private final SQLiteDatabase database;
	private final ModelMap models;
	private final ScanStorageComponent scanStorageComponent;

	public UpdateModelTask(final ModelMap models, final SQLiteDatabase database, final ScanStorageComponent scanStorageComponent) {
		this.models = models;
		this.database = database;
		this.scanStorageComponent = scanStorageComponent;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		System.out.println("UpdateModelTask::doInBackground.");
		final Set<Integer> IDSet = models.keySet();

		final Cursor cursor = ChosenModel.selectChosenDictionaryIDsAndPaths(database);
		final List<Integer> IDListFromCursor = new ArrayList<Integer>();
		final Map<Integer, Dictionary> newModels = new HashMap<Integer, Dictionary>();

		// Prepare new models.
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int dictID = cursor.getInt(cursor.getColumnIndex(ChosenModel.ID_COLUMN));
			IDListFromCursor.add(dictID);
			if(!IDSet.contains(dictID)) {
				try {
					final DictionaryInformation info = DictionaryInformation.newInstance(cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN)));
					final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
					final DictionaryFile dictionaryFile = DictionaryFile.makeRandomAccessFile(info.getDataFile());
					final Dictionary model = new UsedDictionary(indexFile, dictionaryFile);
					newModels.put(dictID, model);
				} catch (final IndexFileNotFoundException e) {
					DictionaryScanner.log(e.getMessage());
				} catch (final DataFileNotFoundException e) {
					DictionaryScanner.log(e.getMessage());
				}
			}
		}

		// Remove old models.
		for(final Integer i : IDSet) {
			if(!IDListFromCursor.contains(i)) {
				models.remove(i);
			}
		}

		// Add new models.
		models.putAll(newModels);

		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		setStartPage();
	}

	private void setStartPage() {
		System.out.println("UpdateModelTask::setStartPage");
		final int dictCount = models.size();
		final String welcomeStr = (dictCount > 1 ? scanStorageComponent.context.getString(R.string.usingDictionaryPlural, dictCount) : scanStorageComponent.context.getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = scanStorageComponent.resultTextMaker.getWelcomeHTML(welcomeStr);
		scanStorageComponent.resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}
}
