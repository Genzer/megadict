package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;

public class DictionaryScanner {
	private final List<Dictionary> dictionaryModels = new ArrayList<Dictionary>();
	private final List<String> dictionaryNames = new ArrayList<String>();

	public void scanDatabase(final Activity activity, final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		clearAllList();
		final List<DictionaryInformation> infos = readFromDatabase(activity, database);
		resetScanner(infos);
	}

	public void scanStorage(final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		clearAllList();
		final List<DictionaryInformation> infos = readFromExternalStorage();
		resetScanner(infos);

		// Truncate the table.
		database.delete(ChosenModel.TABLE_NAME, null, null);

		// Insert dictionary infos to database.
		final ContentValues value = new ContentValues();
		for(int i = 0; i < infos.size(); ++i) {
			value.put(ChosenModel.DICTIONARY_NAME_COLUMN, dictionaryNames.get(i));
			value.put(ChosenModel.DICTIONARY_PATH_COLUMN, infos.get(i).getParentFile().getAbsolutePath());
			value.put(ChosenModel.ENABLED_COLUMN, 0);
			database.insert(ChosenModel.TABLE_NAME, null, value);
		}
	}

	// ========================== Private functions ============================ //
	private void resetScanner(final List<DictionaryInformation> infos) {
		for(final DictionaryInformation info : infos) {
			// Create necessary files.
			final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
			final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
			// Create model and add it to list.
			final DICTDictionary model = new DICTDictionary(indexFile, dictFile);
			dictionaryModels.add(model);
			dictionaryNames.add(model.getName());
		}
	}

	private void clearAllList() {
		dictionaryNames.clear();
		dictionaryModels.clear();
	}

	private List<DictionaryInformation> readFromExternalStorage() throws IndexFileNotFoundException, DataFileNotFoundException {
		// Get external reader.
		final ExternalReader reader = new ExternalReader(ExternalStorage.getExternalDirectory());
		return reader.getInfos();
	}

	private List<DictionaryInformation> readFromDatabase(final Activity activity, final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		// Select chosen dictionaries from database.
		final Cursor cursor = ChosenModel.selectChosenDictionaries(database);
		activity.startManagingCursor(cursor);

		final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final String dictPath = cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN));
			final DictionaryInformation info = new DictionaryInformation(new File(dictPath));
			infos.add(info);
		}
		return infos;
	}

	public List<Dictionary> getDictionaryModels() {
		return dictionaryModels;
	}

	public List<String> getDictionaryNames() {
		return dictionaryNames;
	}
}
