package com.megadict.business.scanning;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.megadict.business.AbstractWorkerTask;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;

public class RescanTask extends AbstractWorkerTask<DictionaryInformation, Void, Pair<Integer, Dictionary>> {
	private final SQLiteDatabase database;

	public RescanTask(final SQLiteDatabase database) {
		super();
		this.database = database;
	}

	@Override
	protected Pair<Integer, Dictionary> doInBackground(final DictionaryInformation... params) {
		final DictionaryInformation info = params[0];

		// Create necessary files.
		final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
		final DictionaryFile dictFile =
				DictionaryFile.makeRandomAccessFile(info.getDataFile());
		// Create model.
		final Dictionary model =
				new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();
		// Insert dictionary infos to database.
		final int dictID =
				ChosenModel.insertDictionary(database, model.getName(), info.getParentFile().getAbsolutePath(), ChosenModel.LOCAL_DICTIONARY, 0);

		return Pair.create(dictID, model);
	}
}
