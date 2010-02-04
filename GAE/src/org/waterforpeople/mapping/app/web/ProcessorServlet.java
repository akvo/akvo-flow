package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.queue.QueueManager;

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
					QueueManager qm = new QueueManager();
					qm.submitDeviceFileProcessing(fileName);
				}
			}
		}
	}

}
