package com.megadict.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.bean.RescanComponent;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.model.Dictionary;
import com.megadict.task.RescanAllTask;
import com.megadict.task.ScanStorageAllTask;
import com.megadict.task.base.BaseScanTask;

public class DictionaryScanner {
	private final ExternalReader externalReader = new ExternalReader(ExternalStorage.getExternalDirectory());
	private static final Map<Integer, Dictionary> MODELS = new ConcurrentHashMap<Integer, Dictionary>();
	private BaseScanTask task = null;
	private static final Logger LOGGER = Logger.getLogger("DictionaryScanner");
	@Deprecated
	private static final List<BaseScanTask> TASKS = new Vector<BaseScanTask>();

	public DictionaryScanner() {
		LOGGER.addHandler(new ConsoleHandler());
	}

	public static void addModel(final int dictionaryID, final Dictionary dictionaryModel) {
		MODELS.put(dictionaryID, dictionaryModel);
	}

	public static void removeModel(final int dictionaryID) {
		MODELS.remove(dictionaryID);
	}

	public static int getDictionaryCount() {
		return MODELS.size();
	}

	public static void log(final String message) {
		LOGGER.warning(message);
	}

	public boolean scanStorage(final Activity activity, final SQLiteDatabase database, final ScanStorageComponent scanStorageComponent) {
		if(task == null || !task.isScanning()) {
			MODELS.clear();
			task = new ScanStorageAllTask(activity, database, scanStorageComponent);
			task.execute();
			return true;
		}
		return false;
	}

	public boolean rescan(final RescanComponent rescanComponent) {
		if(task == null || !task.isScanning()) {
			MODELS.clear();
			task = new RescanAllTask(externalReader, rescanComponent);
			task.execute();
			return true;
		}
		return false;
	}

	//	public boolean updateDictonaryModels(final SQLiteDatabase database) {
	//		final Cursor cursor = ChosenModel.selectChosenDictionaryIDs(database);
	//
	//	}

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

	public List<Dictionary> getDictionaryModels() {
		return new ArrayList<Dictionary>(MODELS.values());
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
