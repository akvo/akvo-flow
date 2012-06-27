/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
