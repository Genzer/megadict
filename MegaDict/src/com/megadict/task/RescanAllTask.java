package com.megadict.task;

import java.util.List;

import android.content.ContentValues;

import com.megadict.bean.RescanComponent;
import com.megadict.business.DictionaryScanner;
import com.megadict.business.ExternalReader;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.UsedDictionary;
import com.megadict.task.base.BaseScanTask;

public class RescanAllTask extends BaseScanTask {
	private final ExternalReader externalReader;
	private final RescanComponent rescanComponent;

	public RescanAllTask(final ExternalReader externalReader, final RescanComponent rescanComponent) {
		this.externalReader = externalReader;
		this.rescanComponent = rescanComponent;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		rescanComponent.progressDialog.show();
	}

	@Override
	protected Void doInBackground(final Void... params) {
		final List<DictionaryInformation> infos = externalReader.getInfos();

		// Truncate the table.
		rescanComponent.database.delete(ChosenModel.TABLE_NAME, null, null);

		for(final DictionaryInformation info : infos) {
			// Create necessary files.
			final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
			final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
			// Create model.
			final Dictionary model = UsedDictionary.newInstance(indexFile, dictFile);
			// Insert dictionary infos to database.
			final ContentValues value = new ContentValues();
			value.put(ChosenModel.DICTIONARY_NAME_COLUMN, model.getName());
			value.put(ChosenModel.DICTIONARY_PATH_COLUMN, info.getParentFile().getAbsolutePath());
			value.put(ChosenModel.ENABLED_COLUMN, 0);
			final int dictID = (int)rescanComponent.database.insert(ChosenModel.TABLE_NAME, null, value);

			// Store model.
			DictionaryScanner.addModel(dictID, model);
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		// Requery the cursor to update list view.
		rescanComponent.cursor.requery();
		// Close progress dialog.
		rescanComponent.progressDialog.dismiss();
	}

	// Need setStartPage() here????? Think!
}
