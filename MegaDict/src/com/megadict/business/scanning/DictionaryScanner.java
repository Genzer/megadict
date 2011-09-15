package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.bean.DictionaryBean;
import com.megadict.bean.DictionaryComponent;
import com.megadict.bean.ManageComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public final class DictionaryScanner extends Observable implements TaskManager {
	public static final String MODEL_CHANGED = "modelChanged";
	private final Context context;
	private OnRefreshStartPageListener onRefreshStartPageListener;

	// This is lazy initialization variables. It can be null if you don't set it.
	private DictionaryComponent dictionaryComponent;

	// Models and tasks.
	private final ModelMap models = new ModelMap();
	private final List<RescanTask> rescanTasks =
			new ArrayList<RescanTask>();
	private final List<ScanTask> scanTasks =
			new ArrayList<ScanTask>();
	private final List<AddWikiTask> wikiTasks =
			new ArrayList<AddWikiTask>();

	public DictionaryScanner(final Context context) {
		super();
		this.context = context;
	}

	// ============= Public functions. ==========//
	public void setDictionaryComponent(final DictionaryComponent dictionaryComponent) {
		this.dictionaryComponent = dictionaryComponent;
		if(!didAllScanTasksFinish() && dictionaryComponentNotNull()) {
			dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
		}
	}

	@Override
	public void scanStorage() {
		// Clear old models.
		models.clear();
		// Clear all tasks.
		scanTasks.clear();

		// Get cursor.
		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(context);
		final Cursor cursor =
				ChosenModel.selectChosenDictionaryIDsAndPaths(database);
		// Execute scan tasks.
		if (cursor.getCount() > 0) {
			executeScanTasks(cursor);
		} else {
			refreshStartPage();
		}
		// Close cursor.
		cursor.close();
	}

	@Override
	public void rescan(final ManageComponent manageComponent) {
		// Remove old models.
		models.clear();
		// Clear all tasks.
		rescanTasks.clear();
		// Change ProgressDialog message.
		manageComponent.setProgressDialogMessage(context.getString(R.string.scanning));

		// Read from external storage.
		final List<DictionaryInformation> infos = ExternalReader.readExternalStorage(ExternalStorage.getExternalDirectory());
		// Get database.
		final SQLiteDatabase database =	DatabaseHelper.getDatabase(context);
		// Truncate the table.
		database.delete(ChosenModel.TABLE_NAME, null, null);
		// Execute rescan tasks.
		if (!infos.isEmpty()) {
			executeRecanTasks(manageComponent, database, infos);
		}
	}

	@Override
	public void updateDictionaryModels() {
		// Clear all tasks.
		scanTasks.clear();

		// Prepare useful lists for updating models.
		final List<Integer> oldIDs = new ArrayList<Integer>(models.keySet());
		final List<Integer> newIDs = new ArrayList<Integer>();
		final List<DictionaryBean> insertedBeans =
				new ArrayList<DictionaryBean>();

		// Get database and cursor.
		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(context);
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
			refreshStartPage();
		} else {
			executeUpdateTasks(insertedBeans);
		}

		// Close cursor.
		cursor.close();
	}

	@Override
	public void addWikiDictionaries(final List<String> countryCodes, final ManageComponent manageComponent) {
		// Clear all tasks.
		wikiTasks.clear();
		// Change ProgressDialog message
		manageComponent.setProgressDialogMessage(context.getString(R.string.adding));

		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(context);

		for (final String code : countryCodes) {
			final AddWikiTask task =
					new AddWikiTask(database);
			setOnPreExecuteForAddWikiTask(task, manageComponent);
			setOnPostExecuteForAddWikiTask(task, manageComponent);
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

	public List<Dictionary> getDictionaryModels() {
		return new ArrayList<Dictionary>(models.values());
	}

	// ========================== Private functions ============================ //
	private void executeScanTasks(final Cursor cursor) {
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final int id = ChosenModel.getID(cursor);
			final String path = ChosenModel.getPath(cursor);
			final String type = ChosenModel.getType(cursor);

			final DictionaryBean bean =
					new DictionaryBean.Builder(id).path(path).type(type).build();
			final ScanTask task = new ScanTask();
			setOnPreExecuteForScanTask(task);
			setOnPostExecuteForScanTask(task);
			task.execute(bean);
			scanTasks.add(task);
		}
	}

	private void executeRecanTasks(final ManageComponent manageComponent, final SQLiteDatabase database, final List<DictionaryInformation> infos) {
		for (final DictionaryInformation info : infos) {
			final RescanTask task = new RescanTask(database);
			setOnPreExecuteForRescanTask(task, manageComponent);
			setOnPostExecuteForRescanTask(task, manageComponent);
			task.execute(info);
			rescanTasks.add(task);
		}
	}

	private void executeUpdateTasks(final List<DictionaryBean> insertedBeans) {
		for (final DictionaryBean bean : insertedBeans) {
			final ScanTask task = new ScanTask();
			setOnPreExecuteForScanTask(task);
			setOnPostExecuteForScanTask(task);
			task.execute(bean);
			scanTasks.add(task);
		}
	}

	private void setOnPreExecuteForScanTask(final ScanTask task) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllScanTasksFinish() && dictionaryComponentNotNull()) {
					dictionaryComponent.getProgressBar().setVisibility(ProgressBar.VISIBLE);
				}
			}
		});
	}

	private void setOnPostExecuteForScanTask(final ScanTask task) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer,Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				if(result != null) models.put(result.first, result.second);

				if (didAllScanTasksFinish()) {
					// Notify observers.
					dictionaryModelsChanged();

					if(dictionaryComponentNotNull()) {
						// Refresh start page.
						refreshStartPage();
						// Hide ProgressBar.
						dictionaryComponent.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
					}
				}
			}
		});
	}

	private void setOnPreExecuteForAddWikiTask(final AddWikiTask task, final ManageComponent manageComponent) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllAddWikiTasksFinish()) {
					manageComponent.getProgressDialog().show();
				}
			}
		});
	}

	private void setOnPostExecuteForAddWikiTask(final AddWikiTask task, final ManageComponent manageComponent) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer,Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				models.put(result.first, result.second);

				if (didAllAddWikiTasksFinish()) {
					endTasksUseManageComponent(manageComponent);
				}
			}
		});
	}

	private void setOnPreExecuteForRescanTask(final RescanTask task, final ManageComponent manageComponent) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllRescanTasksFinish()) {
					manageComponent.getProgressDialog().show();
				}
			}
		});
	}

	private void setOnPostExecuteForRescanTask(final RescanTask task, final ManageComponent manageComponent) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer, Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				if(result != null) models.put(result.first, result.second);

				if (didAllRescanTasksFinish()) {
					endTasksUseManageComponent(manageComponent);
				}
			}
		});
	}

	private void endTasksUseManageComponent(final ManageComponent manageComponent) {
		// Notify observers.
		dictionaryModelsChanged();
		// Requery the cursor to update list view.
		manageComponent.getCursor().requery();
		// Close progress dialog.
		manageComponent.getProgressDialog().dismiss();
	}

	private void refreshStartPage() {
		if(onRefreshStartPageListener != null) {
			onRefreshStartPageListener.resfreshStartPage();
		}
	}

	private boolean dictionaryComponentNotNull() {
		return dictionaryComponent == null ? false : true;
	}

	// ========================== Protected functions ============================ //
	protected void dictionaryModelsChanged() {
		setChanged();
		notifyObservers(getDictionaryModels());
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

	//============== RefreshStartPageListener ===============//
	public void setRefreshStartPageListener(final OnRefreshStartPageListener listner) {
		this.onRefreshStartPageListener = listner;
	}

	public interface OnRefreshStartPageListener {
		void resfreshStartPage();
	}
}
