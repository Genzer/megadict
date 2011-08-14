//package com.megadict.business;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//
//import com.megadict.bean.DictionaryComponent;
//import com.megadict.bean.RescanComponent;
//import com.megadict.model.Dictionary;
//
//public class DictionaryClient {
//	private final DictionaryScanner scanner;
//	private final WikiDictionaryScanner wikiScanner;
//	private final WordSearcher searcher;
//	private final WordRecommender recommender;
//
//	public DictionaryClient() {
//		scanner = new DictionaryScanner();
//		wikiScanner = new WikiDictionaryScanner();
//		searcher = new WordSearcher();
//		recommender = new WordRecommender();
//	}
//
//	// ========================= Public functions ========================= //
//	public void setNoDefinitionString(final String noDefinitionStr) {
//		searcher.setNoDefinitionStr(noDefinitionStr);
//	}
//
//	public void setNoDictionaryString(final String noDictionaryStr) {
//		searcher.setNoDictionaryStr(noDictionaryStr);
//	}
//
//	public boolean lookup(final String word, final DictionaryComponent dictionaryComponent) {
//		return searcher.lookup(word, getAllModels(), dictionaryComponent);
//	}
//
//	public boolean recommend(final String word, final DictionaryComponent dictionaryComponent) {
//		return recommender.recommend(word, getAllModels(), dictionaryComponent);
//	}
//
//	public boolean rescan(final RescanComponent rescanComponent) {
//		return scanner.rescan(rescanComponent);
//	}
//
//	public boolean scanStorage(final Activity activity, final DictionaryComponent dictionaryComponent) {
//		return scanner.scanStorage(activity, dictionaryComponent);
//	}
//
//	public boolean updateDictionaryModels(final Activity activity, final DictionaryComponent dictionaryComponent) {
//		return scanner.updateDictonaryModels(activity, dictionaryComponent);
//	}
//
//	private List<Dictionary> getAllModels() {
//		final List<Dictionary> models = new ArrayList<Dictionary>();
//		models.addAll(scanner.getDictionaryModels());
//		models.addAll(wikiScanner.getDictionaryModels());
//		return models;
//	}
//
//	public boolean isRecommending() {
//		return recommender.isRecommending();
//	}
//
//	public int getDictionaryCount() {
//		return scanner.getDictionaryCount();
//	}
//
//	public DictionaryScanner getScanner() {
//		return scanner;
//	}
//}
