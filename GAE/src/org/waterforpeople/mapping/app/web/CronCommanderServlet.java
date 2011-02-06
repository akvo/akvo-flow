package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.helper.KMLHelper;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.notification.helper.NotificationHelper;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyTaskUtil;
import com.google.appengine.api.datastore.Key;

public class CronCommanderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2287175129835274533L;
	private static final Logger log = Logger
			.getLogger(CronCommanderServlet.class.getName());

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
		} else if ("purgeOrphanJobQueueRecords".equals(action)) {
			purgeOrphanJobQueueRecords();
		} else if ("generateNotifications".equals(action)) {
			generateNotifications();
		}
	}

	private void generateNotifications() {
		NotificationHelper helper = new NotificationHelper();
		helper.execute();
	}

	private void purgeExpiredSurveys() {
		DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
		List<DeviceSurveyJobQueue> dsjqList = dsjqDao
				.listAssignmentsWithEarlierExpirationDate(new Date());
		for (DeviceSurveyJobQueue item : dsjqList) {
			SurveyTaskUtil.spawnDeleteTask("deleteDeviceSurveyJobQueue", item
					.getAssignmentId());
		}
	}

	private void purgeOrphanJobQueueRecords() {
		DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
		SurveyDAO surveyDao = new SurveyDAO();
		List<Key> surveyIdList = surveyDao.listSurveyIds();
		List<Long> ids = new ArrayList<Long>();

		for (Key key : surveyIdList)
			ids.add(key.getId());

		for (DeviceSurveyJobQueue item : dsjqDao.listAllJobsInQueue()) {
			Long dsjqSurveyId = item.getSurveyID();
			Boolean found = ids.contains(dsjqSurveyId);
			if (!found) {
				log.info("found orphan assignmentId: " + item.getAssignmentId()
						+ " id: " + item.getId() + " survey: "
						+ item.getSurveyID() + " for deletion");
				SurveyTaskUtil.spawnDeleteTask("deleteDeviceSurveyJobQueue",
						item.getAssignmentId());

			}
		}
	}
}
