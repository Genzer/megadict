package com.megadict.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.bean.RescanComponent;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.singletask.base.BaseScanTask;
import com.megadict.task.RescanAllTask;
import com.megadict.task.ScanStorageAllTask;
import com.megadict.task.base.BaseScanAllTask;

public class DictionaryScanner {
	private static final List<Dictionary> MODELS = new Vector<Dictionary>();
	private BaseScanAllTask task = null;
	@Deprecated
	private static final List<BaseScanTask> TASKS = new Vector<BaseScanTask>();

	public static void addModel(final Dictionary dictionaryModel) {
		MODELS.add(dictionaryModel);
	}

	public static int getDictionaryCount() {
		return MODELS.size();
	}

	public boolean scanStorageNew(final Activity activity, final SQLiteDatabase database, final ScanStorageComponent scanStorageComponent) throws IndexFileNotFoundException, DataFileNotFoundException {
		if(task == null || !task.isScanning()) {
			MODELS.clear();
			final List<DictionaryInformation> infos = readFromDatabase(activity, database);
			task = new ScanStorageAllTask(infos, scanStorageComponent);
			task.execute();
			return true;
		}
		return false;
	}

	public boolean rescanNew(final RescanComponent rescanComponent) throws IndexFileNotFoundException, DataFileNotFoundException {
		if(task == null || !task.isScanning()) {
			MODELS.clear();
			final List<DictionaryInformation> infos = readFromExternalStorage();
			task = new RescanAllTask(infos, rescanComponent);
			task.execute();
			return true;
		}
		return false;
	}

	// ========================== Private functions ============================ //
	@Deprecated
	private void executeTasks() {
		for(final BaseScanTask task : TASKS) {
			task.execute();
		}
	}

	@Deprecated
	public static boolean didAllTasksFinish() {
		for(final BaseScanTask task : TASKS) {
			if(task.isScanning()) {
				return false;
			}
		}
		return true;
	}

	private List<DictionaryInformation> readFromExternalStorage() throws IndexFileNotFoundException, DataFileNotFoundException {
		// Get external reader.
		final ExternalReader reader = new ExternalReader(ExternalStorage.getExternalDirectory());
		return reader.getInfos();
	}

	private List<DictionaryInformation> readFromDatabase(final Activity activity, final SQLiteDatabase database) throws IndexFileNotFoundException, DataFileNotFoundException {
		// Select chosen dictionaries from database.
		final Cursor cursor = ChosenModel.selectChosenDictionaries(database);
		activity.startManagingCursor(cursor);

		final List<DictionaryInformation> infos = new ArrayList<DictionaryInformation>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final String dictPath = cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_PATH_COLUMN));
			final DictionaryInformation info = new DictionaryInformation(new File(dictPath));
			infos.add(info);
		}
		return infos;
	}

	public List<Dictionary> getDictionaryModels() {
		return MODELS;
	}









	//		if(!infos.isEmpty()) {
	//			final DictionaryInformation info = infos.get(0);
	//			System.out.println(info.getIndexFile().getAbsolutePath());
	//			System.out.println(info.getDataFile().getAbsolutePath());
	//			// Create necessary files.
	//			final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
	//			final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
	//			// Create model and add it to list.
	//			final Dictionary model = new DICTDictionary(indexFile, dictFile);
	//			System.out.println(model.getName());
	//		}
}
