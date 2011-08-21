package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.R;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.ResultTextMaker;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.exception.ScanningException;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public class UpdateTask extends AbstractScanTask {
	private final static int TIMEOUT_IN_SECONDS = 3;
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
	protected Void doInBackground(final Void... params) {
		final Set<Integer> IDSet = models.keySet();

		final List<Callable<Dictionary>> newCallables = new ArrayList<Callable<Dictionary>>();
		final List<Integer> newIDs = new ArrayList<Integer>();

		final List<Integer> IDListFromCursor = new ArrayList<Integer>();

		prepareSomething(IDSet, newCallables, newIDs, IDListFromCursor);

		// Remove old models.
		removeOldModels(IDSet, IDListFromCursor);

		// Invoke runnables which insert new models.
		invokeCallables(newCallables, newIDs);

		return null;
	}

	private void removeOldModels(final Set<Integer> oldCollection, final List<Integer> newCollection) {
		for(final Integer i : oldCollection) {
			if(!newCollection.contains(i)) {
				models.remove(i);
			}
		}
	}

	private void invokeCallables(final List<Callable<Dictionary>> callables, final List<Integer> ids) {
		final ExecutorService service = Executors.newFixedThreadPool(Math.max(1, callables.size()));
		try {
			// Invoke all callables.
			final List<Future<Dictionary>> futures = service.invokeAll(callables);
			// Wait them to finish.
			service.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			final int futureCount = futures.size();
			// Store dictionary and its ID.
			for(int i = 0; i < futureCount; ++i) {
				final Dictionary dictionary = futures.get(i).get();
				models.put(ids.get(i), dictionary);
			}
		} catch (final InterruptedException e) {
			throw new ScanningException(e);
		} catch (final ExecutionException e) {
			throw new ScanningException(e);
		}
	}

	private void prepareSomething(final Set<Integer> IDSet, final List<Callable<Dictionary>> newCallables, final List<Integer> newIDs, final List<Integer> IDListFromCursor) {
		final SQLiteDatabase database = DatabaseHelper.getDatabase(dictionaryComponent.getContext());
		final Cursor cursor = ChosenModel.selectChosenDictionaryIDsPathsAndTypes(database);
		// Prepare new models.
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int dictID = cursor.getInt(cursor.getColumnIndex(ChosenModel.ID_COLUMN));
			final String type = cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_TYPE_COLUMN));
			final String path = cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN));
			IDListFromCursor.add(dictID);
			// If any dictionary is not in current dictionary model list, create it.
			if(!IDSet.contains(dictID)) {
				try {
					final DictionaryInformation info = DictionaryInformation.newInstance(path);
					final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
					final DictionaryFile dictionaryFile = DictionaryFile.makeRandomAccessFile(info.getDataFile());

					if(type.equals(ChosenModel.LOCAL_DICTIONARY)) {
						newCallables.add(new CreateDICTThread(indexFile, dictionaryFile));
					} else {
						newCallables.add(new CreateWikiThread(path));
					}
					newIDs.add(dictID);
				} catch (final IndexFileNotFoundException e) {
					DictionaryScanner.log(e.getMessage());
				} catch (final DataFileNotFoundException e) {
					DictionaryScanner.log(e.getMessage());
				}
			}
		}
		cursor.close();
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
