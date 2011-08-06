package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.bean.RecommendComponent;
import com.megadict.bean.RescanComponent;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.bean.SearchComponent;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.model.Dictionary;

public class DictionaryClient {
	private final DictionaryScanner scanner;
	private final WikiDictionaryScanner wikiScanner;
	private final WordSearcher searcher;
	private final WordRecommender recommender;

	public DictionaryClient() {
		scanner = new DictionaryScanner();
		wikiScanner = new WikiDictionaryScanner();
		searcher = new WordSearcher();
		recommender = new WordRecommender();
	}

	// ========================= Public functions ========================= //
	public void setNoDefinitionString(final String noDefinitionStr) {
		searcher.setNoDefinitionStr(noDefinitionStr);
	}

	public void setNoDictionaryString(final String noDictionaryStr) {
		searcher.setNoDictionaryStr(noDictionaryStr);
	}

	public boolean lookup(final String word, final SearchComponent searchComponent) {
		return searcher.lookup(word, getAllModels(), searchComponent);
	}

	public boolean recommend(final Context context, final String word, final RecommendComponent recommendComponent) {
		return recommender.recommend(context, word, getAllModels(), recommendComponent);
	}

	public boolean rescan(final RescanComponent rescanComponent) throws IndexFileNotFoundException, DataFileNotFoundException {
		//return scanner.rescan(database, dialog, cursor);
		return scanner.rescanNew(rescanComponent);
	}

	public boolean scanStorage(final Activity activity, final SQLiteDatabase database, final ScanStorageComponent scanStorageComponent) throws IndexFileNotFoundException, DataFileNotFoundException {
		//		scanner.scanStorage(activity, database);
		return scanner.scanStorageNew(activity, database, scanStorageComponent);
	}

	public void scanDatabase() {
		// Working
	}

	private List<Dictionary> getAllModels() {
		final List<Dictionary> models = new ArrayList<Dictionary>();
		models.addAll(scanner.getDictionaryModels());
		models.addAll(wikiScanner.getDictionaryModels());
		return models;
	}

	public boolean isRecommending() {
		return recommender.isRecommending();
	}

	public int getDictitionaryCount() {
		return scanner.getDictionaryCount();
	}
}
