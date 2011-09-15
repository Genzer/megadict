package com.megadict.business.scanning;

import java.util.List;

import com.megadict.bean.ManageComponent;

public interface TaskManager {
	boolean didAllRescanTasksFinish();

	boolean didAllScanTasksFinish();

	boolean didAllAddWikiTasksFinish();

	void scanStorage();

	void rescan(final ManageComponent manageComponent);

	void updateDictionaryModels();

	void addWikiDictionaries(final List<String> countryCodes, final ManageComponent manageComponent);
}