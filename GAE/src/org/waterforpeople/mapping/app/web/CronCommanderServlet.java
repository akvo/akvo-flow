package org.waterforpeople.mapping.app.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.helper.KMLHelper;

public class CronCommanderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2287175129835274533L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String action = req.getParameter("action");
		if ("buildMap".equals(action)) {
			KMLHelper kmlHelper = new KMLHelper();
/*			if (kmlHelper.checkCreateNewMap()) {
				Queue mapAssemblyQueue = QueueFactory.getQueue("mapAssembly");
				TaskOptions task = url("/app_worker/mapassembly").param("action",
						action).param("action", "buildMap");
				mapAssemblyQueue.add(task);
			}*/
		}
	}

}
