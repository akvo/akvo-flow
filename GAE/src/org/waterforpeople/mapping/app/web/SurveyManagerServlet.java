package org.waterforpeople.mapping.app.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.DeviceFiles;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;

public class SurveyManagerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Long surveyId = 0L;
		SurveyInstanceDAO siDAO = new SurveyInstanceDAO();
		SurveyInstance si=null;
		if (req.getParameter("surveyId") != null) {
			 surveyId = new Long(req.getParameter("surveyId"));
				si = siDAO.get(surveyId);

		}
		String action = req.getParameter("action");
		String surveyDoc = req.getParameter("surveyDoc");
		String devicePhoneNumber = req.getParameter("devicePhoneNumber");

		resp.setContentType("application/xhtml+xml");
		try {
			if (surveyId != 0L) {
				resp.getWriter().println(si.toString());
				DeviceFiles df = si.getDeviceFile();
				resp.getWriter().println(df.toString());
				for (QuestionAnswerStore qas : si.getQuestionAnswersStore()) {
					resp.getWriter().println(qas.toString());
					resp.getWriter().println(
							"----------SurveyID assoced with QuestionAnswer: "
									+ qas.getSurveyInstance().getId());
				}
			} else if (action.equals("save") && surveyDoc != null) {
				SurveyDAO surveyDAO = new SurveyDAO();
				surveyDAO.save(surveyDoc);
			} else if (action.equals("getAvailableSurveysDevice")
					&& devicePhoneNumber != null) {
				resp.getWriter().print(getSurveyForPhone(devicePhoneNumber));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("getAvailableSurveysDevice")
				&& devicePhoneNumber != null) {
			try {
				resp.setContentType("application/xhtml+xml");
				resp.getWriter().print(getSurveyForPhone(devicePhoneNumber));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private String getSurveyForPhone(String devicePhoneNumber) {
		SurveyDAO surveyDAO = new SurveyDAO();
		DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
		return surveyDAO.getSurveyDocument(dsjqDAO.get(devicePhoneNumber));
	}
	

}
