package org.waterforpeople.mapping.app.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class RawDataImportRequest extends RestRequest {

	private static final long serialVersionUID = 3792808180110794885L;
	private static final DateFormat IN_FMT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss z");
	private static final String VALUE = "value=";

	public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
	public static final String COLLECTION_DATE_PARAM = "collectionDate";
	public static final String QUESTION_ID_PARAM = "questionId";
	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String TYPE_PARAM = "type";

	public static final String SAVE_SURVEY_INSTANCE_ACTION = "saveSurveyInstance";
	public static final String RESET_SURVEY_INSTANCE_ACTION = "resetSurveyInstance";

	private Long surveyId;
	private Long surveyInstanceId = null;
	private Date collectionDate = null;
	private HashMap<Long, String> questionAnswerMap = null;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
		// TODO handle errors

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null)
			setSurveyInstanceId(new Long(req
					.getParameter(SURVEY_INSTANCE_ID_PARAM)));
		if (req.getParameter(QUESTION_ID_PARAM) != null) {
			String[] answers = req.getParameterValues(QUESTION_ID_PARAM);
			if (answers != null) {
				for (int i = 0; i < answers.length; i++) {
					String[] parts = answers[i].split("\\|");
					String qId = null;
					String val = null;
					if (parts.length > 1) {
						qId = parts[0];

						if (parts.length == 2) {
							val = parts[1];
						} else {
							StringBuffer buf = new StringBuffer();
							for (int idx = 1; idx < parts.length; idx++) {
								if (idx > 1) {
									buf.append("|");
								}
								buf.append(parts[idx]);
							}
							val = buf.toString();
						}
						if (val != null) {
							if (val.startsWith(VALUE)) {
								val = val.substring(VALUE.length());
							}
							putQuestionAnswer(new Long(qId), val);
						}
					}

				}
			}
		}
		if (req.getParameter(SURVEY_ID_PARAM) != null) {
			surveyId = new Long(req.getParameter(SURVEY_ID_PARAM).trim());
		}
		if (req.getParameter(COLLECTION_DATE_PARAM) != null) {
			collectionDate = IN_FMT.parse(req.getParameter(
					COLLECTION_DATE_PARAM).trim());
		}
		if (req.getParameter(TYPE_PARAM) != null) {
			type = req.getParameter(TYPE_PARAM).trim();
		}
	}

}
