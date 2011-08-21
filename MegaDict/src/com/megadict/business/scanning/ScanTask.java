package com.megadict.business.scanning;

import com.megadict.bean.DictionaryBean;
import com.megadict.bean.DictionaryComponent;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.wiki.WikiDictionary;

public class ScanTask extends AbstractScanTask {
	private final DictionaryScanner scanner;
	private final DictionaryComponent dictionaryComponent;
	private final ModelMap models;

	public ScanTask(final DictionaryScanner scanner, final ModelMap models,
			final DictionaryComponent dictionaryComponent) {
		super();
		this.scanner = scanner;
		this.models = models;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected Void doInBackground(final DictionaryBean... params) {
		// Get properties from bean.
		final DictionaryBean bean = params[0];
		final int id = bean.getId();
		final String path = bean.getPath();
		final String type = bean.getType();

		try {
			// Create callables.
			Dictionary dictionary;
			if(type.equals(ChosenModel.LOCAL_DICTIONARY)) {
				// Create dict info.
				final DictionaryInformation info = DictionaryInformation.newInstance(path);
				// Create necessary files.
				final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
				final DictionaryFile dictionaryFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
				dictionary = new DICTDictionary(indexFile, dictionaryFile);
			} else {
				dictionary = new WikiDictionary(path);
			}

			// Store models.
			models.put(id, dictionary);
		} catch (final IndexFileNotFoundException e) {
			scanner.log(e.getMessage());
		} catch (final DataFileNotFoundException e) {
			scanner.log(e.getMessage());
		}

		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		if(scanner.didAllScanTasksFinish()) {
			// Notify observers.
			scanner.dictionaryModelsChanged();
			// Refresh start page.
			scanner.refreshStartPage(dictionaryComponent);
		}
	}
}
