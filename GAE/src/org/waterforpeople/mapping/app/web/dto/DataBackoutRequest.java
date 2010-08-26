package org.waterforpeople.mapping.app.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request dto for data backout utility
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataBackoutRequest extends RestRequest {

	private static final long serialVersionUID = -1311252813916737262L;
	public static final String GET_QUESTION_ACTION = "getQuestions";
	public static final String GET_SURVEY_INSTANCE_ACTION = "getSurveyInstances";
	public static final String DELETE_SURVEY_INSTANCE_ACTION = "deleteQuestionAnswer";
	public static final String DELETE_QUESTION_SUMMARY_ACTION = "deleteQuestionSummary";
	public static final String LIST_INSTANCE_ACTION = "listInstance";
	public static final String DELETE_ACCESS_POINT_ACTION = "deleteAccessPoint";
	public static final String DELETE_AP_SUMMARY_ACTION = "deleteAPSummary";

	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
	public static final String QUESTION_ID_PARAM = "questionId";
	public static final String COUNTRY_PARAM = "countryCode";
	public static final String DATE_PARAM = "date";

	private static final DateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd");

	private Long surveyId;
	private Long surveyInstanceId;
	private Long questionId;
	private String countryCode;
	private Date date;
	

	
	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	@Override
	protected void populateErrors() {
		// TODO: add error checking
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(SURVEY_ID_PARAM) != null) {
			surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM).trim());
		}
		if (req.getParameter(QUESTION_ID_PARAM) != null) {
			surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM).trim());
		}
		if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null) {
			surveyInstanceId = Long.parseLong(req.getParameter(
					SURVEY_INSTANCE_ID_PARAM).trim());
		}
		if (req.getParameter(DATE_PARAM) != null) {
			date = inFmt.parse(req.getParameter(DATE_PARAM));
		}
		countryCode = req.getParameter(COUNTRY_PARAM);
	}

}
