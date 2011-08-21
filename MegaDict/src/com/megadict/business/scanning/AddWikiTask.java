package com.megadict.business.scanning;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.megadict.bean.RescanComponent;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;
import com.megadict.wiki.WikiDictionary;

public class AddWikiTask extends AsyncTask<String, Void, Void> {
	private final DictionaryScanner scanner;
	private final RescanComponent rescanComponent;
	private final ModelMap models;
	private boolean scanning;

	public AddWikiTask(final DictionaryScanner scanner, final ModelMap modelMap, final RescanComponent rescanComponent) {
		super();
		this.scanner = scanner;
		this.models = modelMap;
		this.rescanComponent = rescanComponent;
	}

	@Override
	protected void onPreExecute() {
		rescanComponent.getProgressDialog().show();
		scanning = true;
	}

	@Override
	protected Void doInBackground(final String... params) {
		final SQLiteDatabase database = DatabaseHelper.getDatabase(rescanComponent.getContext());

		// Get country code
		final String countryCode = params[0];
		// Create model.
		final Dictionary model = new WikiDictionary(countryCode);
		// Insert dictionary to database.
		final int dictID = ChosenModel.insertDictionary(database, model.getName(), countryCode, ChosenModel.WIKI_DICTIONARY, 0);

		// Store model.
		models.put(dictID, model);
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		scanning = false;

		if(scanner.didAllAddTasksFinish()) {
			scanner.dictionaryModelsChanged();
			// Requery the cursor to update list view.
			rescanComponent.getCursor().requery();
			// Close progress dialog.
			rescanComponent.getProgressDialog().dismiss();
		}
	}

	public boolean isScanning() {
		return scanning;
	}
}
