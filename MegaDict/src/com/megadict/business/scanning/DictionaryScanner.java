package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import android.app.Activity;

import com.megadict.bean.DictionaryComponent;
import com.megadict.bean.RescanComponent;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.model.Dictionary;
import com.megadict.model.ModelMap;

public final class DictionaryScanner extends Observable {
	private final ExternalReader externalReader = new ExternalReader(ExternalStorage.getExternalDirectory());
	private final ModelMap models = new ModelMap();
	private BaseScanTask task = null;
	private static final Logger LOGGER = Logger.getLogger("DictionaryScanner");
	@Deprecated
	private static final List<BaseScanTask> TASKS = new Vector<BaseScanTask>();

	public DictionaryScanner() {
		LOGGER.addHandler(new ConsoleHandler());
	}

	public List<Dictionary> getDictionaryModels() {
		return new ArrayList<Dictionary>(models.values());
	}

	public int getDictionaryCount() {
		return models.size();
	}

	public static void log(final String message) {
		LOGGER.warning(message);
	}

	public boolean scanStorage(final Activity activity, final DictionaryComponent dictionaryComponent) {
		if(task == null || !task.isScanning()) {
			task = new ScanStorageAllTask(this, models, activity, dictionaryComponent);
			task.execute();
			dictionaryModelsChanged();
			return true;
		}
		return false;
	}

	public boolean rescan(final RescanComponent rescanComponent) {
		if(task == null || !task.isScanning()) {
			task = new RescanAllTask(this, models, externalReader, rescanComponent);
			task.execute();
			dictionaryModelsChanged();
			return true;
		}
		return false;
	}

	public boolean updateDictionaryModels(final Activity activity, final DictionaryComponent dictionaryComponent) {
		if(task == null || !task.isScanning()) {
			task = new UpdateModelTask(this, models, activity, dictionaryComponent);
			task.execute();
			return true;
		}
		return false;
	}

	// ========================== Private functions ============================ //
	/* Using package access modifier */
	void dictionaryModelsChanged() {
		setChanged();
		notifyObservers(getDictionaryModels());
	}

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
