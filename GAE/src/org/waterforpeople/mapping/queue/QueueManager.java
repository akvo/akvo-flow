package org.waterforpeople.mapping.queue;

import java.util.logging.Logger;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Builder;


public class QueueManager {
	private static final Logger log = Logger.getLogger(QueueManager.class.getName());
	public void submitDeviceFileProcessing(String fileName) {
		/*
		 * 1. Get Queue 
		 * 2. Create the params for the task 
		 * 3. Submit the task
		 */
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(Builder.url("/task").param(
				"action", "submit").param("fileName", fileName));
		log.info("submiting task for fileName: " + fileName);
	}
}
