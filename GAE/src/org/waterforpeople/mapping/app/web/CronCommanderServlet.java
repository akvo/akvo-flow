package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.helper.KMLHelper;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyTaskUtil;

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
			/*
			 * if (kmlHelper.checkCreateNewMap()) { Queue mapAssemblyQueue =
			 * QueueFactory.getQueue("mapAssembly"); TaskOptions task =
			 * url("/app_worker/mapassembly").param("action",
			 * action).param("action", "buildMap"); mapAssemblyQueue.add(task);
			 * }
			 */
		} else if ("purgeExpiredSurveys".equals(action)) {
			purgeExpiredSurveys();
		}
	}
	
	private void purgeExpiredSurveys(){
		DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
		List<DeviceSurveyJobQueue> dsjqList = dsjqDao
				.listAssignmentsWithEarlierExpirationDate(new Date());
		for (DeviceSurveyJobQueue item : dsjqList) {
			SurveyTaskUtil.spawnDeleteTask("deleteDeviceSurveyJobQueue", item.getAssignmentId());
		}
	}
}
