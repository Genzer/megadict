package com.megadict.business.recommending;

public interface RecommendTaskManager {
	boolean didAllRecommendTasksFinish();

	void recommend(String word);

	void cancelRecommending();
}
