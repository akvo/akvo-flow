package com.gallatinsystems.device.app.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.device.helper.DeviceHelper;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;

public class DeviceManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1979457951988807893L;
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if (action.equals("listDevices")) {
			DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
			List<DeviceSurveyJobQueue> jobs = dsjqDAO.listAllJobsInQueue();
			for (DeviceSurveyJobQueue item : jobs) {
				try {
					resp.getWriter().print(item.toString());
				} catch (IOException e) {
					log.log(Level.SEVERE, "Could not execute list device", e);
				}
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		String outputString = null;
		if (action != null && action.equals("saveDevice")) {
			outputString = createDevice(req);
		} else {
			String devicePhoneNumber = req.getParameter("devicePhoneNumber");
			Long surveyId = new Long(req.getParameter("surveyId"));
			DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
			DeviceSurveyJobQueue dsjq = new DeviceSurveyJobQueue();
			dsjq.setDevicePhoneNumber(devicePhoneNumber);
			dsjq.setSurveyID(surveyId);
			Long deviceId = dsjqDAO.save(dsjq);
			resp.setContentType("text/html");
			outputString = "Device: " + deviceId + " created";
		}

		try {
			resp.getWriter().print(outputString);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Could not execute device operation", e);
		}
	}

	@SuppressWarnings("unused")
	private String createDevice(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		String deviceType = req.getParameter("deviceType");
		String esn = req.getParameter("esn");
		String inServiceDateString = req.getParameter("inServiceDateString");
		String osVersion = req.getParameter("osVersion");
		String countryIdString = req.getParameter("countryIdString");
		String phoneNumber = req.getParameter("devicePhoneNumber");
		String outServiceDateString = req.getParameter("outServiceDateString");

		DeviceHelper deviceHelper = new DeviceHelper();
		phoneNumber = phoneNumber.replace("-", "");
		Device device = new Device();

		device.setDeviceType(Device.DeviceType.CELL_PHONE_ANDROID);

		device.setEsn(esn);
		device.setPhoneNumber(phoneNumber);
		device.setKey(deviceHelper.createDevice(device).getKey());
		sb.append(device.toString());
		return sb.toString();
	}

}
