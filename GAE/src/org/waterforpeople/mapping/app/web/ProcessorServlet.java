package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.queue.QueueManager;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class ProcessorServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ProcessorServlet.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String action = req.getParameter("action");
		String fileName = req.getParameter("fileName");
		if (action != null) {
			log.info("	ProcessorServlet->action->" + action);
			log.info("  ProcessorServlet->action->" + fileName);
			if (action.equals("submit")) {
				if (fileName != null) {
					log.info("about to submit task for fileName: " + fileName);
					// Submit the fileName for processing
					Queue queue = QueueFactory.getDefaultQueue();
					
					queue.add(url("/app_worker/task").param("action", "processFile").param("fileName", fileName));
					log.info("submiting task for fileName: " + fileName);
				}
			}
		}
	}

}
