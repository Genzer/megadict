package com.megadict.business.scanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
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
		if(task == null || !task.isScanning()) {
			task = new ScanStorageAllTask(this, models, activity, dictionaryComponent);
			task.execute();
			dictionaryModelsChanged();
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public boolean rescan(final RescanComponent rescanComponent) {
		boolean result;
		if(task == null || !task.isScanning()) {
			task = new RescanAllTask(this, models, externalReader, rescanComponent);
			task.execute();
			dictionaryModelsChanged();
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public boolean updateDictionaryModels(final Activity activity, final DictionaryComponent dictionaryComponent) {
		boolean result;
		if(task == null || !task.isScanning()) {
			task = new UpdateModelTask(this, models, activity, dictionaryComponent);
			task.execute();
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// ========================== Private functions ============================ //
	protected void dictionaryModelsChanged() {
		setChanged();
		notifyObservers(getDictionaryModels());
	}
}
