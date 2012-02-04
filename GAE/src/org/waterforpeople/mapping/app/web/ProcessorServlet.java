package org.waterforpeople.mapping.app.web;



import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class ProcessorServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7062679258542909086L;
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
					String phoneNumber = req.getParameter("phoneNumber");
					String checksum = req.getParameter("checksum");
					if(checksum == null){
						checksum="null";
					}
					if(phoneNumber == null){
						phoneNumber = "null";
					}
					log.info("about to submit task for fileName: " + fileName);
					// Submit the fileName for processing
					Queue queue = QueueFactory.getDefaultQueue();
					
					queue.add(TaskOptions.Builder.withUrl("/app_worker/task").param("action", "processFile").param("fileName", fileName).param("phoneNumber", phoneNumber).param("checksum", checksum));
					log.info("submiting task for fileName: " + fileName);
				}
			}
		}
	}

}
