package com.megadict.business.scanning;

import android.database.sqlite.SQLiteDatabase;

import com.megadict.bean.RescanComponent;
import com.megadict.business.AbstractWorkerTask;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public class RescanTask extends AbstractWorkerTask<DictionaryInformation, Void, Void> {
	private final DictionaryScanner scanner;
	private final RescanComponent rescanComponent;
	private final ModelMap models;

	public RescanTask(final DictionaryScanner scanner, final ModelMap models, final RescanComponent rescanComponent) {
		super();
		this.scanner = scanner;
		this.rescanComponent = rescanComponent;
		this.models = models;
	}

	@Override
	protected void onPreExecute() {
		if (scanner.didAllRescanTasksFinish()) {
			rescanComponent.getProgressDialog().show();
		}
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(final DictionaryInformation... params) {
		final DictionaryInformation info = params[0];

		// Create necessary files.
		final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
		final DictionaryFile dictFile =
				DictionaryFile.makeRandomAccessFile(info.getDataFile());
		// Create model.
		// final Dictionary model = UsedDictionary.newInstance(indexFile, dictFile);
		final Dictionary model =
				new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();
		// Insert dictionary infos to database.
		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(rescanComponent.getContext());
		final int dictID =
				ChosenModel.insertDictionary(database, model.getName(), info.getParentFile().getAbsolutePath(), ChosenModel.LOCAL_DICTIONARY, 0);

		// Store model.
		models.put(dictID, model);
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		if (scanner.didAllRescanTasksFinish()) {
			scanner.dictionaryModelsChanged();
			// Requery the cursor to update list view.
			rescanComponent.getCursor().requery();
			// Close progress dialog.
			rescanComponent.getProgressDialog().dismiss();
		}
	}
}
