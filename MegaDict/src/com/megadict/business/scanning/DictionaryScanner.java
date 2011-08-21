package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.R;
import com.megadict.bean.DictionaryBean;
import com.megadict.bean.DictionaryComponent;
import com.megadict.bean.RescanComponent;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public final class DictionaryScanner extends Observable implements TaskManager {
	private static final Logger LOGGER = Logger.getLogger("DictionaryScanner");
	private final ExternalReader externalReader = new ExternalReader(ExternalStorage.getExternalDirectory());
	private final ModelMap models = new ModelMap();

	private final List<AbstractRescanTask> rescanTasks = new ArrayList<AbstractRescanTask>();
	private final List<AbstractScanTask> scanTasks = new ArrayList<AbstractScanTask>();
	private final List<AbstractUpdateTask> updateTasks = new ArrayList<AbstractUpdateTask>();
	private final List<AbstractAddWikiTask> wikiTasks = new ArrayList<AbstractAddWikiTask>();

	public DictionaryScanner() {
		super();
		LOGGER.addHandler(new ConsoleHandler());
	}

	public List<Dictionary> getDictionaryModels() {
		return new ArrayList<Dictionary>(models.values());
	}

	public void log(final String message) {
		LOGGER.warning(message);
	}

	public boolean scanStorage(final DictionaryComponent dictionaryComponent) {
		if(didAllScanTasksFinish()) {
			// Clear old models.
			models.clear();
			// Clear all tasks.
			scanTasks.clear();

			// Get cursor.
			final SQLiteDatabase database = DatabaseHelper.getDatabase(dictionaryComponent.getContext());
			final Cursor cursor = ChosenModel.selectChosenDictionaryIDsAndPaths(database);
			// Execute scan tasks.
			if(cursor.getCount() > 0) {
				executeScanTasks(dictionaryComponent, cursor);
			} else {
				refreshStartPage(dictionaryComponent);
			}
			// Close cursor.
			cursor.close();
			return true;
		}
		return false;
	}

	public boolean rescan(final RescanComponent rescanComponent) {
		if(didAllRescanTasksFinish()) {
			// Remove old models.
			models.clear();
			// Clear all tasks.
			rescanTasks.clear();

			// Read from external storage.
			final List<DictionaryInformation> infos = externalReader.getInfos();
			// Get database.
			final SQLiteDatabase database = DatabaseHelper.getDatabase(rescanComponent.getContext());
			// Truncate the table.
			database.delete(ChosenModel.TABLE_NAME, null, null);
			// Execute rescan tasks.
			if(!infos.isEmpty()) {
				executeRecanTasks(rescanComponent, infos);
			}
			return true;
		}
		return false;
	}

	public boolean updateDictionaryModels(final DictionaryComponent dictionaryComponent) {
		if(didAllUpdateTasksFinish()) {
			// Clear all tasks.
			updateTasks.clear();

			// Prepare useful lists for updating models.
			final List<Integer> oldIDs = new ArrayList<Integer>(models.keySet());
			final List<Integer> newIDs = new ArrayList<Integer>();
			final List<DictionaryBean> insertedBeans = new ArrayList<DictionaryBean>();

			// Get database and cursor.
			final SQLiteDatabase database = DatabaseHelper.getDatabase(dictionaryComponent.getContext());
			final Cursor cursor = ChosenModel.selectChosenDictionaryIDsPathsAndTypes(database);

			// Execute update tasks.
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				final int id = ChosenModel.getID(cursor);
				final String path = ChosenModel.getPath(cursor);
				final String type = ChosenModel.getType(cursor);
				final DictionaryBean bean = new DictionaryBean.Builder(id).path(path).type(type).build();
				// Store beans for later inserting.
				if(!oldIDs.contains(id)) {
					insertedBeans.add(bean);
				}
				// Store new ID for later inserting and removing.
				newIDs.add(id);
			}

			// Loop through newIDs, if an ID is in newIDs and not in oldIDs,
			/// we insert its model. (actually we create AsyncTask to insert it)
			removeOldModels(models, oldIDs, newIDs);
			// Execute update tasks.
			if(insertedBeans.isEmpty()) {
				// Notify for observers.
				dictionaryModelsChanged();
				// Refresh start page.
				refreshStartPage(dictionaryComponent);
			} else {
				executeUpdateTasks(dictionaryComponent, insertedBeans);
			}

			// Close cursor.
			cursor.close();
			return true;
		}
		return false;
	}

	public boolean addWikiDictionaries(final List<String> countryCodes, final RescanComponent rescanComponent) {
		if(didAllAddWikiTasksFinish()) {
			// Clear all tasks.
			wikiTasks.clear();

			for(final String code : countryCodes) {
				final AbstractAddWikiTask task = new AddWikiTask(this, models, rescanComponent);
				task.execute(code);
				wikiTasks.add(task);
			}
			return true;
		}
		return false;
	}

	// ========================== Private functions ============================ //
	private void executeScanTasks(final DictionaryComponent dictionaryComponent, final Cursor cursor) {
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int id = ChosenModel.getID(cursor);
			final String path = ChosenModel.getPath(cursor);
			final String type = ChosenModel.getType(cursor);

			final DictionaryBean bean = new DictionaryBean.Builder(id).path(path).type(type).build();
			final AbstractScanTask task = new ScanTask(this, models, dictionaryComponent);
			task.execute(bean);
			scanTasks.add(task);
		}
	}

	private void executeRecanTasks(final RescanComponent rescanComponent, final List<DictionaryInformation> infos) {
		for(final DictionaryInformation info : infos) {
			final AbstractRescanTask task = new RescanTask(this, models, rescanComponent);
			task.execute(info);
			rescanTasks.add(task);
		}
	}

	private void executeUpdateTasks(final DictionaryComponent dictionaryComponent, final List<DictionaryBean> insertedBeans) {
		for(final DictionaryBean bean : insertedBeans) {
			final AbstractUpdateTask task = new UpdateTask(this, models, dictionaryComponent);
			task.execute(bean);
			updateTasks.add(task);
		}
	}

	// ========================== Protected functions ============================ //
	protected void dictionaryModelsChanged() {
		setChanged();
		notifyObservers(getDictionaryModels());
	}

	protected void refreshStartPage(final DictionaryComponent dictionaryComponent) {
		final int dictCount = models.size();
		final String welcomeStr = (dictCount > 1 ?
				dictionaryComponent.getContext().getString(R.string.usingDictionaryPlural, dictCount) :
					dictionaryComponent.getContext().getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = dictionaryComponent.getResultTextMaker().getWelcomeHTML(welcomeStr);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}

	protected void removeOldModels(final ModelMap models, final List<Integer> oldIDs, final List<Integer> newIDs) {
		// Loop through oldIDs, if an ID is in oldIDs and not in newIDs,
		/// we remove its model.
		for(final Integer i : oldIDs) {
			if(!newIDs.contains(i)) {
				models.remove(i);
			}
		}
	}

	//======== Overriden methods from TaskManager. ==========//////////
	@Override
	public boolean didAllRescanTasksFinish() {
		for(final AbstractRescanTask task : rescanTasks) {
			if(task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didAllAddWikiTasksFinish() {
		for(final AbstractAddWikiTask task : wikiTasks) {
			if(task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didAllScanTasksFinish() {
		for(final AbstractScanTask task : scanTasks) {
			if(task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didAllUpdateTasksFinish() {
		for(final AbstractUpdateTask task : updateTasks) {
			if(task.isWorking()) {
				return false;
			}
		}
		return true;
	}

}
