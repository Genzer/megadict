package com.megadict.business.scanning;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.megadict.business.AbstractWorkerTask;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.wiki.WikiDictionary;

class AddWikiTask extends AbstractWorkerTask<String, Void, Pair<Integer, Dictionary>> {
	private final SQLiteDatabase database;

	public AddWikiTask(final SQLiteDatabase database) {
		super();
		this.database = database;
	}

	@Override
	protected Pair<Integer, Dictionary> doInBackground(final String... params) {
		// Get country code
		final String countryCode = params[0];
		// Create model.
		final Dictionary model = new WikiDictionary(countryCode);

		// Insert dictionary to database.
		final int dictID = ChosenModel.insertDictionary(database, model.getName(), countryCode, ChosenModel.WIKI_DICTIONARY, 0);
		return Pair.create(dictID, model);
	}
}
