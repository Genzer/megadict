package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.model.Dictionary;

public class DictionaryClient {
	private final List<Dictionary> dictionaryModels = new ArrayList<Dictionary>();
	private final List<String> dictionaryNames = new ArrayList<String>();
	private final DictionaryScanner scanner;
	private final WordSearcher searcher;
	private final WordRecommender recommender;

	public DictionaryClient() {
		scanner = new DictionaryScanner(dictionaryModels, dictionaryNames);
		searcher = new WordSearcher(dictionaryModels);
		recommender = new WordRecommender(dictionaryModels);
	}

	// ========================= Public functions ========================= //
	public void setNoDefinitionString(final String noDefinitionStr) {
		searcher.setNoDefinitionString(noDefinitionStr);
	}

	public List<String> lookup(final String word) {
		return searcher.lookup(word);
	}

	public List<String> recommend(final String word) {
		return recommender.recommend(word);
	}

	public void scanStorage(final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		scanner.scanStorage(database);
	}

	public void scanDatabase(final Activity activity, final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		scanner.scanDatabase(activity, database);
		System.out.println("Dictionary model count: " + dictionaryModels.size());
	}

	public List<String> getDictionaryNames() {
		return dictionaryNames;
	}

	public void stopSearching() {
		searcher.stopAllTasks();
	}
}
