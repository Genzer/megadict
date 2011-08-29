package com.megadict.business.searching;

public interface SearchTaskManager {
	boolean didAllSearchTasksFinish();

	void search(String word);
}
