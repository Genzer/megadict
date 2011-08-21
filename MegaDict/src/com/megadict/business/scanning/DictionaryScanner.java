package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.megadict.bean.DictionaryComponent;
import com.megadict.bean.RescanComponent;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.model.ChosenModel;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.model.ModelMap;
import com.megadict.utility.DatabaseHelper;

public final class DictionaryScanner extends Observable implements TaskManager {
	private final ExternalReader externalReader = new ExternalReader(ExternalStorage.getExternalDirectory());
	private final ModelMap models = new ModelMap();
	private static final Logger LOGGER = Logger.getLogger("DictionaryScanner");
	private final List<AbstractRescanTask> rescanTasks = new ArrayList<AbstractRescanTask>();
	private final List<AbstractScanTask> scanTasks = new ArrayList<AbstractScanTask>();
	private AbstractScanTask scanTask;
	private final List<AddWikiTask> wikiTasks = new ArrayList<AddWikiTask>();

	public DictionaryScanner() {
		super();
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
		boolean result;
		if(scanTask == null || !scanTask.isWorking()) {
			scanTask = new ScanTask(this, models, dictionaryComponent);
			scanTask.execute();
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public boolean rescan(final RescanComponent rescanComponent) {
		if(didAllTasksFinish()) {
			// Remove old dicts.
			models.clear();
			// Read from external storage.
			final List<DictionaryInformation> infos = externalReader.getInfos();
			// Get database.
			final SQLiteDatabase database = DatabaseHelper.getDatabase(rescanComponent.getContext());
			// Truncate the table.
			database.delete(ChosenModel.TABLE_NAME, null, null);
			// Execute scan tasks.
			executeScanTasks(rescanComponent, infos);
			return true;
		}
		return false;
	}

	private void executeScanTasks(final RescanComponent rescanComponent, final List<DictionaryInformation> infos) {
		for(final DictionaryInformation info : infos) {
			final AbstractRescanTask task = new RescanTask(this, models, rescanComponent);
			task.execute(info);
			rescanTasks.add(task);
		}
	}

	public boolean updateDictionaryModels(final Activity activity, final DictionaryComponent dictionaryComponent) {
		boolean result;
		if(scanTask == null || !scanTask.isWorking()) {
			scanTask = new UpdateTask(this, models, dictionaryComponent);
			scanTask.execute();
			result = true;
		} else {
			result = false;
		}
		return result;
	}


	@Override
	public boolean didAllTasksFinish() {
		for(final AbstractRescanTask task : rescanTasks) {
			if(task.isWorking()) {
				return false;
			}
		}
		return true;
	}

	protected boolean didAllAddTasksFinish() {
		for(final AddWikiTask task : wikiTasks) {
			if(task.isScanning()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean didRemainingTasksFinish(final AbstractScanTask task) {
		//		for(final AbstractScanTask currentTask : scanTask) {
		//			if(currentTask != task && task.isWorking()) {
		//				return false;
		//			}
		//		}
		return true;
	}

	public void addWikiDictionaries(final List<String> countryCodes, final RescanComponent rescanComponent) {
		if(didAllAddTasksFinish()) {
			for(final String code : countryCodes) {
				final AddWikiTask task = new AddWikiTask(this, models, rescanComponent);
				task.execute(code);
				wikiTasks.add(task);
			}
		}
	}

	// ========================== Private functions ============================ //
	protected void dictionaryModelsChanged() {
		setChanged();
		notifyObservers(getDictionaryModels());
	}



}
