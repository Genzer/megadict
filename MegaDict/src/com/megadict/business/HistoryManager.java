package com.megadict.business;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class HistoryManager {
	private final Set<String> historySet = new LinkedHashSet<String>();

	public void save(final String word) {
		if (!historySet.contains(word)) {
			historySet.add(word);
		}
	}

	public Set<String> getWordSet() {
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
