package com.gallatinsystems.survey.dao;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * 
 * utility class to facilitate spawning delete tasks
 * 
 */
public class SurveyTaskUtil {
	public static void spawnDeleteTask(String action, Long id) {
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/app_worker/surveytask")
				.param("action", action).param("id", id.toString()));
	}

}
