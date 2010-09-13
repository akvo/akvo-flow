package org.waterforpeople.mapping.app.web.dto;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class RawDataImportRequest extends RestRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3792808180110794885L;
	private static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
	private static final String COLLECTION_DATE_PARAM = "collectionDate";
	private static final String QUESTION_ID_PARAM = "questionId";
	private static final String SAVE_SURVEY_INSTANCE_ACTION = "saveSurveyInstance";

	private Long surveyInstanceId = null;
	private Date collectionDate = null;
	private HashMap<Long, String> questionAnswerMap = null;

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public HashMap<Long, String> getQuestionAnswerMap() {
		return questionAnswerMap;
	}

	public void setQuestionAnswerMap(HashMap<Long, String> questionAnswerMap) {

		this.questionAnswerMap = questionAnswerMap;
	}

	public void putQuestionAnswer(Long questionId, String value) {
		if (questionAnswerMap == null)
			questionAnswerMap = new HashMap<Long, String>();
		questionAnswerMap.put(questionId, value);
	}

	@Override
	protected void populateErrors() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null)
			setSurveyInstanceId(new Long(req
					.getParameter(SURVEY_INSTANCE_ID_PARAM)));
		else if (req.getParameter(COLLECTION_DATE_PARAM) != null)
			setCollectionDate(new Date(req.getParameter(COLLECTION_DATE_PARAM)));
		else if (req.getParameter(QUESTION_ID_PARAM) != null) {
			// is a questionanswer pair
			String[] parts = req.getParameterValues(QUESTION_ID_PARAM)
					.toString().split("\\|");
			this.putQuestionAnswer(new Long(parts[0]), parts[1]);

		}

	}

}
