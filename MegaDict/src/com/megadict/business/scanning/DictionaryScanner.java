package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.bean.DictionaryBean;
import com.megadict.bean.DictionaryComponent;
import com.megadict.bean.RescanComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.business.ResultTextMaker;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public final class DictionaryScanner extends Observable implements TaskManager {
	public static final String MODEL_CHANGED = "modelChanged";
	private final ResultTextMaker resultTextMaker;

	private final ModelMap models = new ModelMap();
	private final List<RescanTask> rescanTasks =
			new ArrayList<RescanTask>();
	private final List<ScanTask> scanTasks =
			new ArrayList<ScanTask>();
	private final List<UpdateTask> updateTasks =
			new ArrayList<UpdateTask>();
	private final List<AddWikiTask> wikiTasks =
			new ArrayList<AddWikiTask>();

	public DictionaryScanner(final ResultTextMaker resultTextMaker) {
		super();
		this.resultTextMaker = resultTextMaker;
	}

	// ============= Public functions. ==========//
	@Override
	public void scanStorage(final DictionaryComponent dictionaryComponent) {
		// Clear old models.
		models.clear();
		// Clear all tasks.
		scanTasks.clear();

		// Get cursor.
		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(dictionaryComponent.getContext());
		final Cursor cursor =
				ChosenModel.selectChosenDictionaryIDsAndPaths(database);
		// Execute scan tasks.
		if (cursor.getCount() > 0) {
			executeScanTasks(dictionaryComponent, cursor);
		} else {
			refreshStartPage(dictionaryComponent);
		}
		// Close cursor.
		cursor.close();
	}

	@Override
	public void rescan(final RescanComponent rescanComponent) {
		// Remove old models.
		models.clear();
		// Clear all tasks.
		rescanTasks.clear();
		// Change ProgressDialog message.
		rescanComponent.setProgressDialogMessage(R.string.scanning);

		// Read from external storage.
		final List<DictionaryInformation> infos = ExternalReader.readExternalStorage(ExternalStorage.getExternalDirectory());
		// Get database.
		final SQLiteDatabase database =	DatabaseHelper.getDatabase(rescanComponent.getContext());
		// Truncate the table.
		database.delete(ChosenModel.TABLE_NAME, null, null);
		// Execute rescan tasks.
		if (!infos.isEmpty()) {
			executeRecanTasks(rescanComponent, database, infos);
		}
	}

	@Override
	public void updateDictionaryModels(final DictionaryComponent dictionaryComponent) {
		// Clear all tasks.
		updateTasks.clear();

		// Prepare useful lists for updating models.
		final List<Integer> oldIDs = new ArrayList<Integer>(models.keySet());
		final List<Integer> newIDs = new ArrayList<Integer>();
		final List<DictionaryBean> insertedBeans =
				new ArrayList<DictionaryBean>();

		// Get database and cursor.
		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(dictionaryComponent.getContext());
		final Cursor cursor =
				ChosenModel.selectChosenDictionaryIDsPathsAndTypes(database);

		// Execute update tasks.
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int id = ChosenModel.getID(cursor);
			final String path = ChosenModel.getPath(cursor);
			final String type = ChosenModel.getType(cursor);
			final DictionaryBean bean =
					new DictionaryBean.Builder(id).path(path).type(type).build();
			// Store beans for later inserting.
			if (!oldIDs.contains(id)) {
				insertedBeans.add(bean);
			}
			// Store new ID for later inserting and removing.
			newIDs.add(id);
		}

		// Loop through newIDs, if an ID is in newIDs and not in oldIDs,
		// / we insert its model. (actually we create AsyncTask to insert it)
		removeOldModels(models, oldIDs, newIDs);
		// Execute update tasks.
		if (insertedBeans.isEmpty()) {
			// Notify for observers.
			dictionaryModelsChanged();
			// Refresh start page.
			refreshStartPage(dictionaryComponent);
		} else {
			executeUpdateTasks(dictionaryComponent, insertedBeans);
		}

		// Close cursor.
		cursor.close();
	}

	@Override
	public void addWikiDictionaries(final List<String> countryCodes, final RescanComponent rescanComponent) {
		// Clear all tasks.
		wikiTasks.clear();
		// Change ProgressDialog message
		rescanComponent.setProgressDialogMessage(R.string.adding);

		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(rescanComponent.getContext());

		for (final String code : countryCodes) {
			final AddWikiTask task =
					new AddWikiTask(database);
			setOnPreExecuteForAddWikiTask(task, rescanComponent);
			setOnPostExecuteForAddWikiTask(task, rescanComponent);
			task.execute(code);
			wikiTasks.add(task);
		}
	}

	@Override
	public boolean didAllRescanTasksFinish() {
		for (final RescanTask task : rescanTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didAllAddWikiTasksFinish() {
		for (final AddWikiTask task : wikiTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didAllScanTasksFinish() {
		for (final ScanTask task : scanTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didAllUpdateTasksFinish() {
		for (final UpdateTask task : updateTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	public List<Dictionary> getDictionaryModels() {
		return new ArrayList<Dictionary>(models.values());
	}

	// ========================== Private functions ============================ //
	private void executeScanTasks(final DictionaryComponent dictionaryComponent, final Cursor cursor) {
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int id = ChosenModel.getID(cursor);
			final String path = ChosenModel.getPath(cursor);
			final String type = ChosenModel.getType(cursor);

			final DictionaryBean bean =
					new DictionaryBean.Builder(id).path(path).type(type).build();
			final ScanTask task = new ScanTask();
			setOnPreExecuteForScanTask(task, dictionaryComponent);
			setOnPostExecuteForScanTask(task, dictionaryComponent);
			task.execute(bean);
			scanTasks.add(task);
		}
	}

	private void executeRecanTasks(final RescanComponent rescanComponent, final SQLiteDatabase database, final List<DictionaryInformation> infos) {
		for (final DictionaryInformation info : infos) {
			final RescanTask task = new RescanTask(database);
			setOnPreExecuteForRescanTask(task, rescanComponent);
			setOnPostExecuteForRescanTask(task, rescanComponent);
			task.execute(info);
			rescanTasks.add(task);
		}
	}

	private void executeUpdateTasks(final DictionaryComponent dictionaryComponent, final List<DictionaryBean> insertedBeans) {
		for (final DictionaryBean bean : insertedBeans) {
			final UpdateTask task =	new UpdateTask();
			setOnPreExecuteForUpdateTask(task, dictionaryComponent);
			setOnPostExecuteForUpdateTask(task, dictionaryComponent);
			task.execute(bean);
			updateTasks.add(task);
		}
	}

	private void setOnPreExecuteForUpdateTask(final UpdateTask task, final DictionaryComponent dictionaryComponent) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllUpdateTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	private void setOnPostExecuteForUpdateTask(final UpdateTask task, final DictionaryComponent dictionaryComponent) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer, Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				if(result != null) models.put(result.first, result.second);

				if (didAllUpdateTasksFinish()) {
					// Notify for observers.
					dictionaryModelsChanged();
					// Refresh start page.
					refreshStartPage(dictionaryComponent);
					// Hide ProgressBar.
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
				}
			}
		});
	}

	private void setOnPreExecuteForScanTask(final ScanTask task, final DictionaryComponent dictionaryComponent) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllScanTasksFinish()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	private void setOnPostExecuteForScanTask(final ScanTask task, final DictionaryComponent dictionaryComponent) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer,Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				if(result != null) models.put(result.first, result.second);

				if (didAllScanTasksFinish()) {
					// Notify observers.
					dictionaryModelsChanged();
					// Refresh start page.
					refreshStartPage(dictionaryComponent);
					// Hide ProgressBar.
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
				}
			}
		});
	}

	private void setOnPreExecuteForAddWikiTask(final AddWikiTask task, final RescanComponent rescanComponent) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllAddWikiTasksFinish()) {
					rescanComponent.getProgressDialog().show();
				}
			}
		});
	}

	private void setOnPostExecuteForAddWikiTask(final AddWikiTask task, final RescanComponent rescanComponent) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer,Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				// Store model.
				models.put(result.first, result.second);
				if (didAllAddWikiTasksFinish()) {
					dictionaryModelsChanged();
					// Requery the cursor to update list view.
					rescanComponent.getCursor().requery();
					// Close progress dialog.
					rescanComponent.getProgressDialog().dismiss();
				}
			}
		});
	}

	private void setOnPreExecuteForRescanTask(final RescanTask task, final RescanComponent rescanComponent) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllRescanTasksFinish()) {
					rescanComponent.getProgressDialog().show();
				}
			}
		});
	}

	private void setOnPostExecuteForRescanTask(final RescanTask task, final RescanComponent rescanComponent) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer, Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				models.put(result.first, result.second);

				if (didAllRescanTasksFinish()) {
					dictionaryModelsChanged();
					// Requery the cursor to update list view.
					rescanComponent.getCursor().requery();
					// Close progress dialog.
					rescanComponent.getProgressDialog().dismiss();
				}
			}
		});
	}

	// ========================== Protected functions ============================ //
	protected void dictionaryModelsChanged() {
		setChanged();
		notifyObservers(getDictionaryModels());
	}

	private void refreshStartPage(final DictionaryComponent dictionaryComponent) {
		final int dictCount = models.size();
		final String welcomeStr =
				(dictCount > 1
						? dictionaryComponent.getContext().getString(R.string.usingDictionaryPlural, dictCount)
								: dictionaryComponent.getContext().getString(R.string.usingDictionary, dictCount));
		final String welcomeHTML = resultTextMaker.getWelcomeHTML(welcomeStr);
		dictionaryComponent.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
	}

	protected void removeOldModels(final ModelMap models, final List<Integer> oldIDs, final List<Integer> newIDs) {
		// Loop through oldIDs, if an ID is in oldIDs and not in newIDs,
		// / we remove its model.
		for (final Integer i : oldIDs) {
			if (!newIDs.contains(i)) {
				models.remove(i);
			}
		}
	}
}
