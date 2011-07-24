package com.megadict.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.megadict.model.Dictionary;
import com.megadict.thread.SearchThread;

public class WordSearcher {
	private String noDefinitionStr = "There is no definition.";
	private final List<Dictionary> dictionaryModels;
	//	private final List<String> contents = new ArrayList<String>();
	private final List<Callable<String>> callables = new ArrayList<Callable<String>>();
	private ExecutorService service;

	public WordSearcher(final List<Dictionary> dictionaryModels) {
		this.dictionaryModels = dictionaryModels;
	}

	public void setNoDefinitionString(final String noDefinitionStr) {
		this.noDefinitionStr = noDefinitionStr;
	}

	public List<String> lookup(final String word) {
		// Reset contents.
		//		contents.clear();
		callables.clear();

		List<String> contents = new ArrayList<String>();
		service = Executors.newFixedThreadPool(dictionaryModels.size());
		// Lower it. I am not sure if we should lower it.
		final String searchedWord = word.toLowerCase(Locale.ENGLISH);
		for(final Dictionary dict : dictionaryModels) {
			final SearchThread thread = new SearchThread(dict, searchedWord, noDefinitionStr);
			callables.add(thread);
		}

		try {
			final List<Future<String>> futures = service.invokeAll(callables);
			for(final Future<String> future : futures) {
				System.out.println(future.get());
				contents.add(future.get());
			}
		} catch (final InterruptedException e) {
			System.out.println("1" + e.getMessage());
			contents = null;
			//			throw new RuntimeException(e.getCause());
		} catch (final ExecutionException e) {
			System.out.println("2" + e.getMessage());
			contents = null;
			//			throw new RuntimeException(e.getCause());
		}
		service.shutdown();
		return contents;
	}

	public void stopAllTasks() {
		if(service != null && !service.isShutdown()) {
			System.out.println("StopAllTasks");
			service.shutdownNow();
		}
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
