package com.gallatinsystems.survey.app.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;
import com.gallatinsystems.survey.domain.xml.SurveyGroupAssoc;

public class SurveyGroupServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9158765660952256159L;

	private static final Logger log = Logger.getLogger(SurveyGroupServlet.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		String outString = new String();
		BaseDAO<SurveyGroup> surveyGroupDAO = new BaseDAO<SurveyGroup>(SurveyGroup.class);
		if (action != null && action.equals("addSurveyGroup")) {
			String code = req.getParameter("code");
			String description = req.getParameter("description");
			SurveyGroup surveyGroup = new SurveyGroup();
			surveyGroup.setCode(code);
			surveyGroup.setDescription(description);		
			surveyGroupDAO.save(surveyGroup);
			outString = surveyGroup.toString();
		} else if (action != null && action.equals("associateSurveyGroup")) {
			String surveyGroupFromCode = req.getParameter("surveyGroupFrom");
			String surveyGroupToCode = req.getParameter("surveyGroupTo");
			SurveyGroupAssoc surveyGroupAssoc = new SurveyGroupAssoc();
			surveyGroupAssoc.setSurveyGroupFromCode(surveyGroupFromCode);
			surveyGroupAssoc.setSurveyGroupToCode(surveyGroupToCode);		
			surveyGroupDAO.save(surveyGroupAssoc);
			outString = surveyGroupAssoc.toString();
		} else if (action != null
				&& action.equals("associateSurveyToSurveyGroup")) {
					
			Long surveyContainerId = new Long(req
					.getParameter("surveyContainerId"));
			Long surveyGroupId = new Long(req.getParameter("surveyGroupId"));
			SurveySurveyGroupAssoc ssga = new SurveySurveyGroupAssoc();
			ssga.setSurveyContainerId(surveyContainerId);
			ssga.setSurveyGroupId(surveyGroupId);
			surveyGroupDAO.save(ssga);
			outString = ssga.toString();
		}
		resp.setContentType("text/html");
		try {
			resp.getWriter().print(outString);
		} catch (IOException e) {
			log.log(Level.SEVERE,"could not perform survey group operation",e);
		}
	}

}
