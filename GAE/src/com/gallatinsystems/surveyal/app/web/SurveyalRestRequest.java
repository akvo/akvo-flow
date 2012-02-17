package com.gallatinsystems.surveyal.app.web;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request class for the SurveyalServlet.  
 * 
 * @author Christopher Fagiani
 *
 */
public class SurveyalRestRequest extends RestRequest {

	private static final long serialVersionUID = -1002622416183902696L;
	public static final String INGEST_INSTANCE_ACTION = "ingestInstance";
	public static final String RERUN_ACTION = "rerun";
	public static final String SURVEY_INSTANCE_PARAM = "surveyInstanceId";
	public static final String SURVEY_ID_PARAM ="surveyId";
	

	private Long surveyInstanceId;
	private Long surveyId;

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(SURVEY_INSTANCE_PARAM) != null) {
			try {
				surveyInstanceId = Long.parseLong(req.getParameter(
						SURVEY_INSTANCE_PARAM).trim());
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, SURVEY_INSTANCE_PARAM
								+ " must be an Integer"));
			}
		}
		if(req.getParameter(SURVEY_ID_PARAM)!=null){
			try {
				setSurveyId(Long.parseLong(req.getParameter(
						SURVEY_ID_PARAM).trim()));
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
								+ " must be an Integer"));
			}
		}

	}

	@Override
	protected void populateErrors() {

	}

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyId() {
		return surveyId;
	}
	
	

}
