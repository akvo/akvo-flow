package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.SurveyQuestion;
import org.waterforpeople.mapping.domain.SurveyQuestion.QuestionAnswerType;
import org.waterforpeople.mapping.helper.GeoRegionHelper;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;

public class TestHarnessServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());
	private static final long serialVersionUID = -5673118002247715049L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if ("testBaseDomain".equals(action)) {
			SurveyDAO surveyDAO = new SurveyDAO();
			surveyDAO.test();
			String outString = surveyDAO.getForTest();
			BaseDAO<AccessPoint> pointDao = new BaseDAO<AccessPoint>(AccessPoint.class);
			AccessPoint point = new AccessPoint();
			point.setLatitude(78d);
			point.setLongitude(43d);
			pointDao.save(point);
			try {
				resp.getWriter().print(outString);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not execute test", e);
			}
		} else if ("testSaveRegion".equals(action)) {
			GeoRegionHelper geoHelp = new GeoRegionHelper();
			ArrayList<String> regionLines = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				StringBuilder builder = new StringBuilder();
				builder.append("1,").append("" + i).append(",test,").append(
						20 + i + ",").append(30 + i + "\n");
				regionLines.add(builder.toString());
			}
			geoHelp.processRegionsSurvey(regionLines);
			try {
				resp.getWriter().print("Save complete");
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not save test region", e);
			}
		}else if ("testSurveyQuestion".equals(action)){
			SurveyDAO surveyDao = new SurveyDAO();
			SurveyQuestion q = new SurveyQuestion();
			q.setId("q6");
			q.setText("Source");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
			q = new SurveyQuestion();
			q.setId("q7");
			q.setText("Collection Point");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
			
			SurveyQuestionSummary summ = new SurveyQuestionSummary();
			summ.setQuestionId("q6");
			summ.setCount(new Long(10));
			summ.setResponse("Spring");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q6");
			summ.setCount(new Long(7));
			summ.setResponse("Borehole");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q6");
			summ.setCount(new Long(22));
			summ.setResponse("Surface Water");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q7");
			summ.setCount(new Long(19));
			summ.setResponse("Public Handpump");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q7");
			summ.setCount(new Long(32));
			summ.setResponse("Yard Connection");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q7");
			summ.setCount(new Long(3));
			summ.setResponse("House Connection");
			surveyDao.save(summ);		
					
		}
	}	
}
