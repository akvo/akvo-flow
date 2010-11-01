package org.waterforpeople.mapping.app.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionHelpMediaDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.TranslationDao;

public class SurveyTaskServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger.getLogger(SurveyTaskServlet.class
			.getName());
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9064136783930675167L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyTaskRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		SurveyTaskRequest stReq = (SurveyTaskRequest) req;
		Long id = stReq.getId();
		log.log(Level.INFO, "action: " + stReq.getAction() + " id: " + id);
		if (stReq.getAction().equals(SurveyTaskRequest.DELETE_SURVEY_ACTION)) {
			SurveyDAO surveyDao = new SurveyDAO();
			surveyDao.delete(surveyDao.getByKey(id));
		} else if (stReq.getAction().equals(
				SurveyTaskRequest.DELETE_QUESTION_GROUP_ACTION)) {
			QuestionGroupDao qgDao = new QuestionGroupDao();
			qgDao.delete(qgDao.getByKey(id));
		} else if (stReq.getAction().equals(
				SurveyTaskRequest.DELETE_QUESTION_ACTION)) {
			QuestionDao qDao = new QuestionDao();
			qDao.delete(qDao.getByKey(id));
		} else if (stReq.getAction().equals(
				SurveyTaskRequest.DELETE_QUESTION_OPTION_ACTION)) {
			QuestionOptionDao qoDao = new QuestionOptionDao();
			qoDao.delete(qoDao.getByKey(id));
		} else if (stReq.getAction().equals(
				SurveyTaskRequest.DELETE_QUESTION_HELP_ACTION)) {
			QuestionHelpMediaDao qhDao = new QuestionHelpMediaDao();
			qhDao.delete(qhDao.getByKey(id));
		} else if (stReq.getAction().equals(
				SurveyTaskRequest.DELETE_QUESTION_TRANSLATION_ACTION)) {
			TranslationDao tDao = new TranslationDao();
			tDao.delete(tDao.getByKey(id));
		}else if(stReq.getAction().equals("deleteDeviceSurveyJobQueue")){
			DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
			dsjqDao.deleteJob(id);
		}
		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// TODO Auto-generated method stub

	}

}
