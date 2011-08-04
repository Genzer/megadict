package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import com.megadict.model.Dictionary;


public class WikiDictionaryScanner {
	private final List<Dictionary> dictionaryModels = new ArrayList<Dictionary>();

	public List<Dictionary> getDictionaryModels() {
		return dictionaryModels;
	}
}
