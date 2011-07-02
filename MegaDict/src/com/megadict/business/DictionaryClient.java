package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import model.Definition;
import android.util.Pair;
import format.dict.DICTDictionary;

public class DictionaryClient {
	private final List<DICTDictionary> dictionaryModels = new ArrayList<DICTDictionary>();

	public void scanChosenDictionaries(final List<Pair<String, String>> chosenDictionaries) {
		dictionaryModels.clear();
		for(final Pair<String, String> dict : chosenDictionaries) {
			dictionaryModels.add(new DICTDictionary(dict.first, dict.second));
		}
	}

	public String lookup(final String word) {
		// Just get the first dictionary. This will be improved later.
		if(dictionaryModels.size() > 0) {
			final DICTDictionary dictionaryModel = dictionaryModels.get(0);
			final Definition d = dictionaryModel.lookUp(word);
			return d.getContent();
		}
		return "Please choose a dictionary.";
	}
}
