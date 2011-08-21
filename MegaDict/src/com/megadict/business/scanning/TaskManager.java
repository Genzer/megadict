package com.megadict.business.scanning;

public interface TaskManager {
	public boolean didAllTasksFinish();
	public boolean didRemainingTasksFinish(AbstractScanTask task);
}
