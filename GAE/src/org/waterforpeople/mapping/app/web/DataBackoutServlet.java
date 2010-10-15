package org.waterforpeople.mapping.app.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.web.dto.DataBackoutRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;

/**
 * servlet for backing out survey response data (and corresponding
 * summarizations)
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataBackoutServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 4608959174864994769L;

	private QuestionDao qDao;
	private SurveyQuestionSummaryDao questionSummaryDao;
	private SurveyInstanceDAO instanceDao;
	private AccessPointDao accessPointDao;
	private AccessPointStatusSummaryDao apSummaryDao;
	private static final DateFormat OUT_FMT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss z");

	public DataBackoutServlet() {
		setMode(PLAINTEXT_MODE);
		qDao = new QuestionDao();
		questionSummaryDao = new SurveyQuestionSummaryDao();
		instanceDao = new SurveyInstanceDAO();
		accessPointDao = new AccessPointDao();
		apSummaryDao = new AccessPointStatusSummaryDao();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new DataBackoutRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		DataBackoutRequest boReq = (DataBackoutRequest) req;
		RestResponse response = new RestResponse();
		if (DataBackoutRequest.GET_QUESTION_ACTION.equals(boReq.getAction())) {
			response.setMessage(listQuestionIds(boReq.getSurveyId()));
		} else if (DataBackoutRequest.DELETE_QUESTION_SUMMARY_ACTION
				.equals(boReq.getAction())) {
			deleteQuestionSummary(boReq.getQuestionId());
		} else if (DataBackoutRequest.LIST_INSTANCE_ACTION.equals(boReq
				.getAction())) {
			response.setMessage(listSurveyInstance(boReq.getSurveyId(), boReq
					.includeDate()));
		} else if (DataBackoutRequest.DELETE_SURVEY_INSTANCE_ACTION
				.equals(boReq.getAction())) {
			deleteSurveyInstance(boReq.getSurveyInstanceId());
		} else if (DataBackoutRequest.DELETE_ACCESS_POINT_ACTION.equals(boReq
				.getAction())) {
			response
					.setMessage(""
							+ deleteAccessPoint(boReq.getCountryCode(), boReq
									.getDate()));
		} else if (DataBackoutRequest.DELETE_AP_SUMMARY_ACTION.equals(boReq
				.getAction())) {
			response.setMessage(""
					+ deleteAccessPointSummary(boReq.getCountryCode(), boReq
							.getDate()));
		} else if (DataBackoutRequest.LIST_INSTANCE_RESPONSE_ACTION
				.equals(boReq.getAction())) {
			response.setMessage(listResponses(boReq.getSurveyInstanceId()));
		}
		return response;
	}

	
	/**
	 * lists all questionAnswerStore records for a given instance
	 * 
	 * @param surveyInstanceId
	 * @return
	 */
	private String listResponses(Long surveyInstanceId) {
		StringBuilder result = new StringBuilder();
		List<QuestionAnswerStore> qasList = instanceDao
				.listQuestionAnswerStore(surveyInstanceId, null);
		if (qasList != null) {
			boolean isFirst = true;
			for (QuestionAnswerStore qas : qasList) {
				if (!isFirst) {
					result.append("\n");
				} else {
					isFirst = false;
				}
				result.append(qas.getQuestionID()).append(",").append(
						qas.getValue());
			}
		}
		return result.toString();
	}

	/**
	 * deletes all access point status summary objects for the country specified
	 * with a creation date on or after the date passed in. This method will
	 * delete 20 records at a time. If there are more remaining, it will return
	 * true, otherwise it will return false
	 * 
	 * @param country
	 * @param creationDate
	 * @return
	 */
	private boolean deleteAccessPointSummary(String country, Date creationDate) {
		boolean hasMore = false;
		List<AccessPointStatusSummary> apList = apSummaryDao
				.listByCountryAndCreationDate(country, creationDate, null);
		if (apList != null) {
			if (apList.size() == BaseDAO.DEFAULT_RESULT_COUNT) {
				hasMore = true;
			}
			accessPointDao.delete(apList);
		}
		return hasMore;
	}

	/**
	 * deletes all access points in the country specified with a collection date
	 * on or after the date passed in. This method will delete 20 records at a
	 * time. If there are more remaining, it will return true, otherwise it will
	 * return false
	 * 
	 * @param country
	 * @param collectionDateFrom
	 * @return
	 */
	private boolean deleteAccessPoint(String country, Date collectionDateFrom) {
		boolean hasMore = false;
		List<AccessPoint> apList = accessPointDao.searchAccessPoints(country,
				null, collectionDateFrom, null, null, null, null, null, null,
				null, null);
		if (apList != null) {
			if (apList.size() == BaseDAO.DEFAULT_RESULT_COUNT) {
				hasMore = true;
			}
			accessPointDao.delete(apList);
		}
		return hasMore;
	}

	/**
	 * returns a comma separated list of survyeInstanceIds for the survey passed
	 * in
	 * 
	 * @param surveyId
	 * @return
	 */
	private String listSurveyInstance(Long surveyId, boolean includeDate) {
		List<SurveyInstance> instances = instanceDao
				.listSurveyInstanceBySurvey(surveyId, null);
		StringBuilder buffer = new StringBuilder();
		if (instances != null) {
			boolean isFirst = true;
			for (SurveyInstance i : instances) {
				if (!isFirst) {
					buffer.append(",");
				} else {
					isFirst = false;
				}
				buffer.append(i.getKey().getId());
				if (includeDate && i.getCollectionDate() != null) {
					buffer.append("|").append(
							OUT_FMT.format(i.getCollectionDate()));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * deletes a survye instance and it's associated questionAnswerStore objects
	 * 
	 * @param surveyInstanceId
	 */
	private void deleteSurveyInstance(Long surveyInstanceId) {
		List<QuestionAnswerStore> questions = instanceDao
				.listQuestionAnswerStore(surveyInstanceId, null);
		if (questions != null) {
			instanceDao.delete(questions);
		}
		SurveyInstance instance = instanceDao.getByKey(surveyInstanceId);
		if (instance != null) {
			instanceDao.delete(instance);
		}
	}

	/**
	 * deletes all the SurveyQuestionSummary objects for a specific questionId
	 * 
	 * @param questionId
	 */
	private void deleteQuestionSummary(Long questionId) {
		List<SurveyQuestionSummary> summaries = questionSummaryDao
				.listByQuestion(questionId.toString());
		if (summaries != null) {
			questionSummaryDao.delete(summaries);
		}
	}

	/**
	 * returns a comma separated list of question IDs contained in the survey
	 * passed in
	 * 
	 * @param surveyId
	 * @return
	 */
	private String listQuestionIds(Long surveyId) {
		List<Question> questions = qDao.listQuestionsBySurvey(surveyId);
		StringBuilder buffer = new StringBuilder();
		if (questions != null) {
			boolean isFirst = true;
			for (Question q : questions) {
				if (!isFirst) {
					buffer.append(",");
				} else {
					isFirst = false;
				}
				buffer.append(q.getKey().getId());
			}
		}
		return buffer.toString();
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		getResponse().getWriter().println(resp.getMessage());
	}
}
