package org.waterforpeople.mapping.app.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class RawDataImportRequest extends RestRequest {

	private static final long serialVersionUID = 3792808180110794885L;
	private static final DateFormat IN_FMT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss z");
	private static final String VALUE = "value=";
	private static final String TYPE = "type=";

	public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
	public static final String COLLECTION_DATE_PARAM = "collectionDate";
	public static final String QUESTION_ID_PARAM = "questionId";
	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String SUBMITTER_PARAM = "submitter";
	public static final String FIXED_FIELD_VALUE_PARAM = "values";
	public static final String LOCALE_ID_PARAM = "surveyedLocale";

	public static final String SAVE_SURVEY_INSTANCE_ACTION = "saveSurveyInstance";
	public static final String RESET_SURVEY_INSTANCE_ACTION = "resetSurveyInstance";
	public static final String SAVE_FIXED_FIELD_SURVEY_INSTANCE_ACTION = "ingestFixedFormat";
	public static final String UPDATE_SUMMARIES_ACTION = "updateSummaries";

	public static final String FIELD_VAL_DELIMITER = ";;";

	private Long surveyId;
	private Long surveyedLocaleId;
	private Long surveyInstanceId = null;
	private Date collectionDate = null;
	private String submitter = null;
	private HashMap<Long, String[]> questionAnswerMap = null;
	private List<String> fixedFieldValues;

	public List<String> getFixedFieldValues() {
		return fixedFieldValues;
	}

	public void setFixedFieldValues(List<String> fixedFieldValues) {
		this.fixedFieldValues = fixedFieldValues;
	}

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

	public HashMap<Long, String[]> getQuestionAnswerMap() {
		return questionAnswerMap;
	}

	public void setQuestionAnswerMap(HashMap<Long, String[]> questionAnswerMap) {

		this.questionAnswerMap = questionAnswerMap;
	}

	public void putQuestionAnswer(Long questionId, String value, String type) {
		if (questionAnswerMap == null)
			questionAnswerMap = new HashMap<Long, String[]>();
		questionAnswerMap.put(questionId, new String[] { value,
				(type != null ? type : "VALUE") });
	}

	@Override
	protected void populateErrors() {
		// TODO handle errors

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(LOCALE_ID_PARAM) != null) {
			try {
				setSurveyedLocaleId(new Long(req.getParameter(LOCALE_ID_PARAM)));
			} catch (Exception e) {
				// swallow
			}
		}
		if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null) {
			try {
				setSurveyInstanceId(new Long(
						req.getParameter(SURVEY_INSTANCE_ID_PARAM)));
			} catch (Exception e) {
				// swallow
			}
		}
		if (req.getParameter(FIXED_FIELD_VALUE_PARAM) != null) {
			fixedFieldValues = new ArrayList<String>();
			String[] vals = req.getParameter(FIXED_FIELD_VALUE_PARAM).split(
					FIELD_VAL_DELIMITER);
			for (int i = 0; i < vals.length; i++) {
				fixedFieldValues.add(vals[i]);
			}
		}
		if (req.getParameter(QUESTION_ID_PARAM) != null) {
			String[] answers = req.getParameterValues(QUESTION_ID_PARAM);
			if (answers != null) {
				for (int i = 0; i < answers.length; i++) {
					String[] parts = answers[i].split("\\|");
					String qId = null;
					String val = null;
					String type = null;
					if (parts.length > 1) {
						qId = parts[0];

						if (parts.length == 3) {
							val = parts[1];
							type = parts[2];
						} else {
							StringBuffer buf = new StringBuffer();
							for (int idx = 1; idx < parts.length - 1; idx++) {
								if (idx > 1) {
									buf.append("|");
								}
								buf.append(parts[idx]);
							}
							val = buf.toString();
							type = parts[parts.length - 1];
						}
						if (val != null) {
							if (val.startsWith(VALUE)) {
								val = val.substring(VALUE.length());
							}
							if (type.startsWith(TYPE)) {
								type = type.substring(TYPE.length());
							}
							if (val != null && val.contains("^^")) {
								val = val.replaceAll("\\^\\^", "|");
							}
							putQuestionAnswer(new Long(qId), val, type);
						}
					}

				}
			}
		}
		if (req.getParameter(SURVEY_ID_PARAM) != null) {
			surveyId = new Long(req.getParameter(SURVEY_ID_PARAM).trim());
		}
		if (req.getParameter(COLLECTION_DATE_PARAM) != null
				&& req.getParameter(COLLECTION_DATE_PARAM).trim().length() > 0	) {
			collectionDate = IN_FMT.parse(req.getParameter(
					COLLECTION_DATE_PARAM).trim());
		}
		if (req.getParameter(SUBMITTER_PARAM) != null) {
			setSubmitter(req.getParameter(SUBMITTER_PARAM));
		}
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getSubmitter() {
		return submitter;
	}

	public Long getSurveyedLocaleId() {
		return surveyedLocaleId;
	}

	public void setSurveyedLocaleId(Long surveyedLocaleId) {
		this.surveyedLocaleId = surveyedLocaleId;
	}

}
