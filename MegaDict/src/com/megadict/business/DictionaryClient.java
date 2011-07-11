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
import com.megadict.model.ChosenModel;
import com.megadict.model.Definition;
import com.megadict.model.DictionaryInformation;

public class DictionaryClient {
	private final List<DICTDictionary> dictionaryModels = new ArrayList<DICTDictionary>();
	private final List<String> names = new ArrayList<String>();
	private final List<String> logger = new ArrayList<String>();

	// ========================= Public functions ========================= //
	public List<String> lookup(final String word, final String dump) {
		final List<String> contents = new ArrayList<String>();
		for(final DICTDictionary dict : dictionaryModels) {
			final Definition d = dict.lookUp(word);
			final String content = d.getContent();
			contents.add(content);
		}
		return contents;
	}

	public List<String> getLogger()
	{
		return logger;
	}

	public List<String> getDictionaryNames()
	{
		return names;
	}

	public void scanDatabase(final Activity activity, final SQLiteDatabase database) {
		final List<DictionaryInformation> infos = getChosenDictionaryInfos(activity, database);
		resetClient(infos);
	}

	public void scanStorage(final SQLiteDatabase database) {
		// Get dictionary infos.
		final List<DictionaryInformation> infos = readExternalStorage();
		resetClient(infos);

		// Truncate the table.
		database.delete(ChosenModel.TABLE_NAME, null, null);

		// Get dictionary names.
		final List<String> dictNames = getDictionaryNames();

		// Insert dictionary infos to database.
		final ContentValues value = new ContentValues();
		for(int i = 0; i < infos.size(); ++i) {
			value.put(ChosenModel.DICTIONARY_NAME_COLUMN, dictNames.get(i));
			value.put(ChosenModel.DICTIONARY_PATH_COLUMN, infos.get(i).getParentFile().getAbsolutePath());
			value.put(ChosenModel.ENABLED_COLUMN, 0);
			database.insert(ChosenModel.TABLE_NAME, null, value);
		}
	}


	// ========================== Private functions ============================ //
	private void resetClient(final List<DictionaryInformation> infos) {
		clearAllList();
		for(final DictionaryInformation info : infos) {
			// Create dictionary model ony when the index file and the dict file exists.
			final String indexFilePath = info.getIndexFile().getAbsolutePath();
			final String dataFilePath = info.getDataFile().getAbsolutePath();
			final DICTDictionary model = new DICTDictionary(indexFilePath, dataFilePath);
			dictionaryModels.add(model);
			names.add(model.getName());
		}
	}

	private void clearAllList() {
		names.clear();
		dictionaryModels.clear();
		logger.clear();
	}

	private List<DictionaryInformation> readExternalStorage() {
		// Get external reader.
		final ExternalReader reader = new ExternalReader(ExternalStorage.getExternalDirectory());

		// Append logger messages.
		final List<String> otherLogger = reader.getLogger();
		logger.addAll(otherLogger);
		return reader.getInfos();
	}

	private List<DictionaryInformation> getChosenDictionaryInfos(final Activity activity, final SQLiteDatabase database) {
		// Select chosen dictionaries from database.
		final Cursor cursor = ChosenModel.selectChosenDictionaries(database);
		activity.startManagingCursor(cursor);

		final List<DictionaryInformation> list = new ArrayList<DictionaryInformation>();
		try {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				final String dictPath = cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN));
				final DictionaryInformation info = new DictionaryInformation(new File(dictPath));
				list.add(info);
			}
		} catch (final IndexFileNotFoundException e) {
			logger.add(e.getMessage());
		} catch (final DataFileNotFoundException e) {
			logger.add(e.getMessage());
		}
		return list;
	}
}
