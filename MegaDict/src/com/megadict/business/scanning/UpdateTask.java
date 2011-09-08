package com.megadict.business.scanning;

import android.widget.ProgressBar;

import com.megadict.bean.DictionaryBean;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask;
import com.megadict.exception.FileNotFoundException;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.MegaLogger;
import com.megadict.wiki.WikiDictionary;

public class UpdateTask extends AbstractWorkerTask<DictionaryBean, Void, Void> {
	private final DictionaryScanner scanner;
	private final ModelMap models;
	private final DictionaryComponent dictionaryComponent;

	public UpdateTask(final DictionaryScanner scanner, final ModelMap models, final DictionaryComponent dictionaryComponent) {
		super();
		this.scanner = scanner;
		this.models = models;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected void onPreExecute() {
		if (scanner.didAllUpdateTasksFinish()) {
			dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
		}
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(final DictionaryBean... params) {
		// Get properties from bean.
		final DictionaryBean bean = params[0];
		final int id = bean.getId();
		final String type = bean.getType();
		final String path = bean.getPath();

		try {
			Dictionary dictionary;
			if (type.equals(ChosenModel.LOCAL_DICTIONARY)) {
				final DictionaryInformation info =
						DictionaryInformation.newInstance(path);
				final IndexFile indexFile =
						IndexFile.makeFile(info.getIndexFile());
				final DictionaryFile dictionaryFile =
						DictionaryFile.makeRandomAccessFile(info.getDataFile());
				dictionary =
						new DICTDictionary.Builder(indexFile, dictionaryFile).enableSplittingIndexFile().build();
			} else {
				dictionary = new WikiDictionary(path);
			}
			models.put(id, dictionary);
		} catch (final FileNotFoundException e) {
			MegaLogger.log(e.getMessage());
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		if (scanner.didAllUpdateTasksFinish()) {
			// Notify for observers.
			scanner.dictionaryModelsChanged();
			// Refresh start page.
			scanner.refreshStartPage(dictionaryComponent);
			// Hide ProgressBar.
			dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
		}
	}
}
