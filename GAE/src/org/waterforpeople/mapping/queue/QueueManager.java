package org.waterforpeople.mapping.queue;



import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.ProcessingAction;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;


public class QueueManager {
	private static final Logger log = Logger.getLogger(QueueManager.class.getName());
	public void submitDeviceFileProcessing(String fileName) {
		/*
		 * 1. Get Queue 
		 * 2. Create the params for the task 
		 * 3. Submit the task
		 */
		Queue queue = QueueFactory.getDefaultQueue();
		
		log.info(url("/app_worker/task").param("action", "process").param("fileName", fileName).toString());
		queue.add(url("/app_worker/task").param("action", "process").param("fileName", fileName));
		log.info("submiting task for fileName: " + fileName);
	}
	
	public void submitNewTaskToQueue(ProcessingAction pa){
		Queue queue = QueueFactory.getDefaultQueue();
		
	}
}
