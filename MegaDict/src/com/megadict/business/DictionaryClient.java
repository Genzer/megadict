package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

import com.megadict.format.dict.DICTDictionary;
import com.megadict.model.Definition;

public class DictionaryClient {
	private final List<DICTDictionary> dictionaryModels = new ArrayList<DICTDictionary>();

	public void scanChosenDictionaries(final List<Pair<String, String>> chosenDictionaries) {
		System.out.println("Before clear dictionary model list.");
		dictionaryModels.clear();
		for(final Pair<String, String> dict : chosenDictionaries) {
			// Create dictionary model ony when the index file and the dict file exists.
			if( (new File(dict.first).exists()) && (new File(dict.second).exists()) ) {
				System.out.println(dict.first + "   " + dict.second);
				dictionaryModels.add(new DICTDictionary(dict.first, dict.second));
			}
		}

		//		dictionaryModels.add(new DICTDictionary("/mnt/sdcard/Download/megadict/VE/vn_en.index",
		//		"/mnt/sdcard/Download/megadict/VE/vn_en.dict"));
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
