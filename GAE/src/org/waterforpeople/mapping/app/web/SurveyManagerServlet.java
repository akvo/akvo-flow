package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;

public class SurveyManagerServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(SurveyManagerServlet.class.getName());

	private static final long serialVersionUID = 4400244780977729721L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Long surveyId = 0L;
		String surveyInstanceKey = null;
		SurveyInstanceDAO siDAO = new SurveyInstanceDAO();
		String surveyDocOut = null;
		if (req.getParameter("surveyInstanceId") != null) {
			surveyInstanceKey = req.getParameter("surveyInstanceId");
			siDAO.getByKey(surveyInstanceKey);

		} else if (req.getParameter("surveyId") != null) {
			surveyId = new Long(req.getParameter("surveyId"));
			surveyDocOut = siDAO.getSurveyDocument(surveyId)
					.getSurveyDocument().getValue();
			resp.setContentType("application/xhtml+xml");
			try {
				resp.getWriter().print(surveyDocOut);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not write survey doc to response",
						e);
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		String surveyDoc = req.getParameter("surveyDocument");
		String devicePhoneNumber = req.getParameter("devicePhoneNumber");

		if (surveyDoc != null) {
			SurveyDAO surveyDAO = new SurveyDAO();
			try {
				resp.getWriter().print("Survey : " + surveyDAO.save(surveyDoc));
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not write survey doc to response",
						e);
			}
		} else if (action.equals("getAvailableSurveysDevice")
				&& devicePhoneNumber != null) {
			try {
				resp.setContentType("application/xhtml+xml");
				resp.getWriter().print(getSurveyForPhone(devicePhoneNumber));
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not write survey doc to response",
						e);
			}
		}

	}

	/*
	 * private String getSurveyForPhone(String devicePhoneNumber) { SurveyDAO
	 * surveyDAO = new SurveyDAO(); DeviceSurveyJobQueueDAO dsjqDAO = new
	 * DeviceSurveyJobQueueDAO(); StringBuilder sb = new StringBuilder();
	 * sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	 * sb.append("<deviceSurveyMapping>"); for (DeviceSurveyJobQueue dsjq :
	 * dsjqDAO.get(devicePhoneNumber)) {
	 * 
	 * sb.append("<deviceSurveyMapItem devicePhoneNumber=\"" + devicePhoneNumber
	 * + "\" surveyId=\"" + dsjq.getSurveyID() + "\"/>"); }
	 * sb.append("</deviceSurveyMapping>"); return sb.toString(); }
	 */
	private String getSurveyForPhone(String devicePhoneNumber) {
		DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
		StringBuilder sb = new StringBuilder();
		for (DeviceSurveyJobQueue dsjq : dsjqDAO.get(devicePhoneNumber)) {
			sb.append(devicePhoneNumber + "," + dsjq.getSurveyID() + ","
					+ dsjq.getName() + "," + dsjq.getLanguage() + ",1.0\n");
		}
		return sb.toString();
	}
}
