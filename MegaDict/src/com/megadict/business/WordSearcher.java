package com.megadict.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.megadict.exception.SearchingException;
import com.megadict.model.Dictionary;
import com.megadict.thread.SearchThread;

public class WordSearcher {
	private String noDefinitionStr = "There is no definition.";
	private final List<Callable<String>> callables = new ArrayList<Callable<String>>();
	final List<String> contents = new ArrayList<String>();
	private ExecutorService service;

	public void setNoDefinitionString(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	public List<String> lookup(final String word, final List<Dictionary> dictionaryModels) {
		clearAllLists();

		if(dictionaryModels.isEmpty()) return contents;

		// Lower it. I am not sure if we should lower it.
		final String searchedWord = word.toLowerCase(Locale.ENGLISH);
		for(final Dictionary dict : dictionaryModels) {
			final SearchThread thread = new SearchThread(dict, searchedWord, noDefinitionStr);
			callables.add(thread);
		}

		service = Executors.newFixedThreadPool(dictionaryModels.size());
		try {
			final List<Future<String>> futures = service.invokeAll(callables);
			for(final Future<String> future : futures) {
				contents.add(future.get());
			}
			return contents;
		} catch (final InterruptedException e) {
			throw new SearchingException(e.getCause());
		} catch (final ExecutionException e) {
			throw new SearchingException(e.getCause());
		} finally {
			service.shutdown();
		}
	}

	private void clearAllLists() {
		contents.clear();
		callables.clear();
	}

	//	public List<String> lookup(final String word) {
	//		// Reset all list.
	//		searchThreads.clear();
	//		contents.clear();
	//
	//
	//
	//		// Lower it. I am not sure if we should lower it.
	//		final String searchedWord = word.toLowerCase(Locale.ENGLISH);
	//		// Create and start search threads.
	//		for(final Dictionary dict : dictionaryModels) {
	//			final SearchThread thread = new SearchThread(dict, searchedWord, noDefinitionStr);
	//			searchThreads.add(thread);
	//			thread.start();
	//		}
	//
	//		// Wait all search tasks finish.
	//		for(final SearchThread thread : searchThreads) {
	//			thread.join();
	//			contents.add(thread.getContent());
	//		}
	//
	//		return contents;
	//	}

	//	public List<String> lookupFirstDict(final String word) {
	//		contents.clear();
	//		if(!dictionaryModels.isEmpty()) {
	//			final Definition d = dictionaryModels.get(0).lookUp(word);
	//			contents.add(d.getContent());
	//		}
	//		return contents;
	//	}
}
