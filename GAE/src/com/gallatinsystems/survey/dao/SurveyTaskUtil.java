package com.gallatinsystems.survey.dao;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class SurveyTaskUtil {
	public static void spawnDeleteTask(String action, Long id) {
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(url("/app_worker/surveytask")
				.param("action", action).param("id",
						id.toString()));
	}

}
