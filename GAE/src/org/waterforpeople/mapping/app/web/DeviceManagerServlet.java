package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.domain.DeviceSurveyJobQueue;

public class DeviceManagerServlet extends HttpServlet{
	private static final Logger log = Logger
	.getLogger(DeviceManagerServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if(action.equals("listDevices")){
			DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
			List<DeviceSurveyJobQueue> jobs = dsjqDAO.listAllJobsInQueue();
			for(DeviceSurveyJobQueue item: jobs){
				try {
					resp.getWriter().print(item.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String devicePhoneNumber = req.getParameter("devicePhoneNumber");
		Long surveyId = new Long(req.getParameter("surveyId"));
		DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
		DeviceSurveyJobQueue dsjq = new DeviceSurveyJobQueue();
		dsjq.setDevicePhoneNumber(devicePhoneNumber);
		dsjq.setSurveyID(surveyId);
		Long deviceId = dsjqDAO.save(dsjq);
		resp.setContentType("text/html");
		try {
			resp.getWriter().print("Device: " + deviceId + " created");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

}
