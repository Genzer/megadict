package com.megadict.business.scanning;

public interface TaskManager {
	boolean didAllRescanTasksFinish();
	boolean didAllScanTasksFinish();
	boolean didAllUpdateTasksFinish();
	boolean didAllAddWikiTasksFinish();
}
