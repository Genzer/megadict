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
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;

public class DictionaryClient {
	private final List<Dictionary> dictionaryModels = new ArrayList<Dictionary>();
	private final List<String> names = new ArrayList<String>();
	private final List<String> logger = new ArrayList<String>();
	private final List<SearchTask> searchTasks = new ArrayList<SearchTask>();
	final List<String> contents = new ArrayList<String>();

	// ========================= Public functions ========================= //
	public List<String> lookup(final String word) {
		// Reset all list.
		searchTasks.clear();
		contents.clear();

		// Create and start search threads.
		for(final Dictionary dict : dictionaryModels) {
			final SearchTask task = new SearchTask(dict, word);
			searchTasks.add(task);
			task.start();
		}

		// Wait all search tasks finish.
		for(final SearchTask task : searchTasks) {
			task.join();
			contents.add(task.getContent());
		}

		return contents;
	}

	public List<String> lookupFirstDict(final String word) {
		contents.clear();
		if(!dictionaryModels.isEmpty()) {
			final Definition d = dictionaryModels.get(0).lookUp(word);
			contents.add(d.getContent());
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
			// Create necessary files.
			final IndexFile indexFile = IndexFile.makeFile(indexFilePath);
			final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile(dataFilePath);
			// Create model and add it to list.
			final DICTDictionary model = new DICTDictionary(indexFile, dictFile);
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


	// ============================ Search task inner class =============== //
	private class SearchTask implements Runnable {
		private static final String CONTENT_NOT_FOUND = "No definition found.";
		private final Dictionary dictionaryModel;
		private final String word;
		private String content;
		private final Thread thread;

		SearchTask(final Dictionary dictionaryModel, final String word) {
			this.dictionaryModel = dictionaryModel;
			this.word = word;
			content = CONTENT_NOT_FOUND;
			thread = new Thread(this);
		}

		public void start() {
			thread.start();
		}

		public void join() {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				logger.add(e.getMessage());
			}
		}

		@Override
		public void run() {
			final Definition d = dictionaryModel.lookUp(word);
			content = d.getContent();
		}

		public String getContent() {
			return content;
		}
	}
}
