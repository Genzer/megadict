package com.megadict.business.scanning;

import android.util.Pair;

import com.megadict.bean.DictionaryBean;
import com.megadict.business.AbstractWorkerTask;
import com.megadict.exception.FileNotFoundException;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.utility.MegaLogger;
import com.megadict.wiki.WikiDictionary;

public class ScanTask extends AbstractWorkerTask<DictionaryBean, Void, Pair<Integer, Dictionary>> {

	@Override
	protected Pair<Integer, Dictionary> doInBackground(final DictionaryBean... params) {
		// Get properties from bean.
		final DictionaryBean bean = params[0];
		final int id = bean.getId();
		final String path = bean.getPath();
		final String type = bean.getType();

		try {
			// Create callables.
			Dictionary dictionary;
			if (type.equals(ChosenModel.LOCAL_DICTIONARY)) {
				// Create dict info.
				final DictionaryInformation info =
						DictionaryInformation.create(path);
				// Create necessary files.
				final IndexFile indexFile =
						IndexFile.makeFile(info.getIndexFile());
				final DictionaryFile dictionaryFile =
						DictionaryFile.makeRandomAccessFile(info.getDataFile());
				dictionary =
						new DICTDictionary.Builder(indexFile, dictionaryFile).enableSplittingIndexFile().build();
			} else {
				dictionary = new WikiDictionary(path);
			}
			return Pair.create(id, dictionary);
		} catch (final FileNotFoundException e) {
			MegaLogger.log(e.getMessage());
			return null;
		}
	}
}
