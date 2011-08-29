package com.megadict.business.scanning;

import java.util.List;

import com.megadict.bean.DictionaryComponent;
import com.megadict.bean.RescanComponent;

public interface TaskManager {
	boolean didAllRescanTasksFinish();

	boolean didAllScanTasksFinish();

	boolean didAllUpdateTasksFinish();

	boolean didAllAddWikiTasksFinish();

	void scanStorage(final DictionaryComponent dictionaryComponent);

	void rescan(final RescanComponent rescanComponent);

	void updateDictionaryModels(final DictionaryComponent dictionaryComponent);

	void addWikiDictionaries(final List<String> countryCodes, final RescanComponent rescanComponent);
}