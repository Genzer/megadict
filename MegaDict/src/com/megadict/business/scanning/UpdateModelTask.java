package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.database.Cursor;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.model.UsedDictionary;

public class UpdateModelTask extends BaseScanTask {
	private final DictionaryScanner scanner;
	private final Activity activity;
	private final ModelMap models;
	private final DictionaryComponent dictionaryComponent;

	public UpdateModelTask(final DictionaryScanner scanner, final ModelMap models, final Activity activity, final DictionaryComponent dictionaryComponent) {
		super();
		this.scanner = scanner;
		this.models = models;
		this.activity = activity;
		this.dictionaryComponent = dictionaryComponent;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		final Set<Integer> IDSet = models.keySet();

		final Cursor cursor = ChosenModel.selectChosenDictionaryIDsAndPaths(dictionaryComponent.getDatabase());
		activity.startManagingCursor(cursor);

		final List<Integer> IDListFromCursor = new ArrayList<Integer>();
		final Map<Integer, Dictionary> newModels = new HashMap<Integer, Dictionary>();

		// Prepare new models.
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int dictID = cursor.getInt(cursor.getColumnIndex(ChosenModel.ID_COLUMN));
			IDListFromCursor.add(dictID);
			if(!IDSet.contains(dictID)) {
				try {
					final DictionaryInformation info = DictionaryInformation.newInstance(cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN)));
					final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
					final DictionaryFile dictionaryFile = DictionaryFile.makeRandomAccessFile(info.getDataFile());
					final Dictionary model = UsedDictionary.newInstance(indexFile, dictionaryFile);
					newModels.put(dictID, model);
				} catch (final IndexFileNotFoundException e) {
					DictionaryScanner.log(e.getMessage());
				} catch (final DataFileNotFoundException e) {
					DictionaryScanner.log(e.getMessage());
				}
			}
		}

		// Remove old models.
		for(final Integer i : IDSet) {
			if(!IDListFromCursor.contains(i)) {
				models.remove(i);
			}
		}

		// Add new models.
		models.putAll(newModels);

		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		scanner.dictionaryModelsChanged();
		setStartPage();
	}

	private void setStartPage() {
		final int dictCount = models.size();
		final String welcomeStr = (dictCount > 1 ?
				dictionaryComponent.getContext().getString(R.string.usingDictionaryPlural, dictCount) :
					dictionaryComponent.getContext().getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = dictionaryComponent.getResultTextMaker().getWelcomeHTML(welcomeStr);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}
}
