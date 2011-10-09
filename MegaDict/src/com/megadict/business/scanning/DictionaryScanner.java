package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.ProgressBar;

import com.megadict.R;
import com.megadict.bean.DictionaryBean;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.AbstractWorkerTask.OnPreExecuteListener;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public final class DictionaryScanner {
	public static final String MODEL_CHANGED = "modelChanged";

	// Aggregation variables.
	private final Context context;

	// Listeners.
	private OnAfterCompleteScanListener onAfterCompleteScanListener;
	private OnCompleteScanListener onCompleteScanListener;
	private OnCompleteRescanListener onCompleteRescanListener;

	// This is lazy initialization variables. It can be null.
	private ProgressBar progressBar;

	// Models and tasks.
	private final ModelMap models = new ModelMap();
	private final List<RescanTask> rescanTasks = new ArrayList<RescanTask>();
	private final List<ScanTask> scanTasks = new ArrayList<ScanTask>();
	private final List<AddWikiTask> wikiTasks =	new ArrayList<AddWikiTask>();

	public DictionaryScanner(final Context context) {
		this.context = context;
	}

	// ============= Public functions. ==========//
	public void updateProgressBar(final ProgressBar progressBar) {
		this.progressBar = progressBar;
		if(!didAllScanTasksFinish() && progressBar != null) {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
	}

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
			afterCompleteScan();
		}
		// Close cursor.
		cursor.close();
	}

	public void rescan(final ProgressDialog progressDialog) {
		// Remove old models.
		models.clear();
		// Clear all tasks.
		rescanTasks.clear();
		// Change ProgressDialog message.
		progressDialog.setMessage(context.getString(R.string.scanning));

		// Read from external storage.
		final List<DictionaryInformation> infos = ExternalReader.readExternalStorage(ExternalStorage.getExternalDirectory());
		// Get database.
		final SQLiteDatabase database =	DatabaseHelper.getDatabase(context);
		// Truncate the table.
		database.delete(ChosenModel.TABLE_NAME, null, null);
		// Execute rescan tasks.
		if (!infos.isEmpty()) {
			executeRecanTasks(progressDialog, database, infos);
		}
	}

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

		// Store new IDs and inserted beans.
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
			completeScan();
			afterCompleteScan();
		} else {
			executeUpdateTasks(insertedBeans);
		}

		// Close cursor.
		cursor.close();
	}

	public void addWikiDictionaries(final List<String> countryCodes, final ProgressDialog progressDialog) {
		// Clear all tasks.
		wikiTasks.clear();
		// Change ProgressDialog message
		progressDialog.setMessage(context.getString(R.string.adding));

		final SQLiteDatabase database =
				DatabaseHelper.getDatabase(context);

		for (final String code : countryCodes) {
			final AddWikiTask task =
					new AddWikiTask(database);
			setOnPreExecuteForAddWikiTask(task, progressDialog);
			setOnPostExecuteForAddWikiTask(task, progressDialog);
			task.execute(code);
			wikiTasks.add(task);
		}
	}

	public boolean didAllRescanTasksFinish() {
		for (final RescanTask task : rescanTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	public boolean didAllAddWikiTasksFinish() {
		for (final AddWikiTask task : wikiTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	public boolean didAllScanTasksFinish() {
		for (final ScanTask task : scanTasks) {
			if (task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	public List<Dictionary> getDictionaryModels() {
		return Collections.unmodifiableList(new ArrayList<Dictionary>(models.values()));
	}

	public int getDictionaryModelCount() {
		return models.size();
	}

	public void setOnAfterCompleteScanListener(final OnAfterCompleteScanListener onAfterCompleteScanListener) {
		this.onAfterCompleteScanListener = onAfterCompleteScanListener;
	}

	public void setOnCompleteScanListener(final OnCompleteScanListener onCompleteScanListener) {
		this.onCompleteScanListener = onCompleteScanListener;
	}

	public void setOnCompleteRescanListener(final OnCompleteRescanListener onCompleteRescanListener) {
		this.onCompleteRescanListener = onCompleteRescanListener;
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

	private void executeRecanTasks(final ProgressDialog progressDialog, final SQLiteDatabase database, final List<DictionaryInformation> infos) {
		for (final DictionaryInformation info : infos) {
			final RescanTask task = new RescanTask(database);
			setOnPreExecuteForRescanTask(task, progressDialog);
			setOnPostExecuteForRescanTask(task, progressDialog);
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
				if (didAllScanTasksFinish() && progressBar != null) {
					progressBar.setVisibility(ProgressBar.VISIBLE);
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
					completeScan();

					if(progressBar != null) {
						afterCompleteScan();

						// Hide ProgressBar.
						progressBar.setVisibility(ProgressBar.INVISIBLE);
					}
				}
			}
		});
	}

	private void setOnPreExecuteForAddWikiTask(final AddWikiTask task, final ProgressDialog progressDialog) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllAddWikiTasksFinish()) {
					progressDialog.show();
				}
			}
		});
	}

	private void setOnPostExecuteForAddWikiTask(final AddWikiTask task, final ProgressDialog progressDialog) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer,Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				models.put(result.first, result.second);

				if (didAllAddWikiTasksFinish()) {
					completeScan();
					completeRescan();
					progressDialog.dismiss();
				}
			}
		});
	}

	private void setOnPreExecuteForRescanTask(final RescanTask task, final ProgressDialog progressDialog) {
		task.setOnPreExecuteListener(new OnPreExecuteListener() {
			@Override
			public void onPreExecute() {
				if (didAllRescanTasksFinish()) {
					progressDialog.show();
				}
			}
		});
	}

	private void setOnPostExecuteForRescanTask(final RescanTask task, final ProgressDialog progressDialog) {
		task.setOnPostExecuteListener(new OnPostExecuteListener<Pair<Integer, Dictionary>>() {
			@Override
			public void onPostExecute(final Pair<Integer, Dictionary> result) {
				if(result != null) models.put(result.first, result.second);

				if (didAllRescanTasksFinish()) {
					completeScan();
					completeRescan();
					progressDialog.dismiss();
				}
			}
		});
	}

	private void afterCompleteScan() {
		if(onAfterCompleteScanListener != null) {
			onAfterCompleteScanListener.onAfterCompleteScan();
		}
	}

	private void completeScan() {
		if(onCompleteScanListener != null) {
			onCompleteScanListener.onCompleteScan(Collections.unmodifiableList(new ArrayList<Dictionary>(models.values())));
		}
	}

	private void completeRescan() {
		if(onCompleteRescanListener != null) {
			onCompleteRescanListener.onCompleteRescan();
		}
	}

	private void removeOldModels(final ModelMap models, final List<Integer> oldIDs, final List<Integer> newIDs) {
		// Loop through oldIDs, if an ID is in oldIDs and not in newIDs,
		// / we remove its model.
		for (final Integer i : oldIDs) {
			if (!newIDs.contains(i)) {
				models.remove(i);
			}
		}
	}

	// ================ Interfaces ==============//
	public interface OnAfterCompleteScanListener {
		void onAfterCompleteScan();
	}

	public interface OnCompleteScanListener {
		void onCompleteScan(List<Dictionary> dictionaryModels);
	}

	public interface OnCompleteRescanListener {
		void onCompleteRescan();
	}
}
