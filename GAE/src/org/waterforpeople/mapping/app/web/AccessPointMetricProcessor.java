package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.AccessPointMetricSummarizer;

public class AccessPointMetricProcessor extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(AccessPointMetricProcessor.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 4375353186699165558L;
	static Integer processcount = 0;
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String key = req.getParameter("key");
		AccessPointMetricSummarizer apms = new AccessPointMetricSummarizer();
		apms.performSummarization(key, null, null, null, null);
		++processcount;
		if (processcount % 50 == 0)
			log.log(Level.INFO, "Processed : " + processcount);
		resp.setStatus(200);
	}

}
