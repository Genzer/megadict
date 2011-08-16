package com.megadict.business;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class HistoryManager {
	private final SortedSet<String> historySet = new TreeSet<String>();

	public HistoryManager() {
		historySet.add("person");
		historySet.add("personal");
		historySet.add("something");
		historySet.add("damn");
	}

	public void save(final String word) {
		historySet.add(word);
	}

	public SortedSet<String> getWordSet() {
		return historySet;
	}

	public void clear() {
		historySet.clear();
	}

	public void remove(final String word) {
		historySet.remove(word);
	}

	public void removeAll(final Collection<String> collection) {
		historySet.removeAll(collection);
	}
}
