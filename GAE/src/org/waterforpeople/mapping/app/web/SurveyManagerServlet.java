package org.waterforpeople.mapping.app.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.DeviceFiles;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

public class SurveyManagerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Long surveyId = new Long(req.getParameter("surveyId"));
		SurveyInstanceDAO siDAO = new SurveyInstanceDAO();
		SurveyInstance si = siDAO.get(surveyId);
		resp.setContentType("text/plain");
		try {
			if (si != null) {
				resp.getWriter().println(si.toString());
				DeviceFiles df = si.getDeviceFile();
				resp.getWriter().println(df.toString());
				for (QuestionAnswerStore qas : si.getQuestionAnswersStore()) {
					resp.getWriter().println(qas.toString());
					resp.getWriter().println(
							"----------SurveyID assoced with QuestionAnswer: "
									+ qas.getSurveyInstance().getId());
				}
			} else {
				resp.getWriter().println("No Survey found for id: " + surveyId);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		String surveyDoc = req.getParameter("surveyDoc");
		// Survey survey = new SurveyXMLAdapter().unmarshall(surveyDoc);
		SurveyDAO surveyDAO = new SurveyDAO();
		surveyDAO.save(surveyDoc);
		resp.setContentType("text/plain");
	}

}
