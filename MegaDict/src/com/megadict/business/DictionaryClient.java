package com.megadict.business;

import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;

public class DictionaryClient {
	private final DictionaryScanner scanner;
	private final WordSearcher searcher;
	private final WordRecommender recommender;

	public DictionaryClient() {
		scanner = new DictionaryScanner();
		searcher = new WordSearcher();
		recommender = new WordRecommender();
	}

	// ========================= Public functions ========================= //
	public void setNoDefinitionString(final String noDefinitionStr) {
		searcher.setNoDefinitionString(noDefinitionStr);
	}

	public List<String> lookup(final String word) {
		return searcher.lookup(word, scanner.getDictionaryModels());
	}

	public List<String> recommend(final String word) {
		return recommender.recommend(word, scanner.getDictionaryModels());
	}

	public void scanStorage(final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		scanner.scanStorage(database);
	}

	public void scanDatabase(final Activity activity, final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		scanner.scanDatabase(activity, database);
	}

	public List<String> getDictionaryNames() {
		return scanner.getDictionaryNames();
	}
}
