package org.waterforpeople.mapping.app.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.survey.dao.SurveyDAO;

public class TestHarnessServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5673118002247715049L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp){
		String action = req.getParameter("action");
		if(action.equals("testBaseDomain")){
			SurveyDAO surveyDAO = new SurveyDAO();
			surveyDAO.test();
			String outString = surveyDAO.getForTest();
			try {
				resp.getWriter().print(outString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}

}
