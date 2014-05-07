/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.web.dto;

import java.net.URLDecoder;
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
	private static final ThreadLocal<DateFormat> IN_FMT = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
		};
	};
	private static final String VALUE = "value=";
	private static final String TYPE = "type=";

	public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
	public static final String COLLECTION_DATE_PARAM = "collectionDate";
	public static final String QUESTION_ID_PARAM = "questionId";
	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String SUBMITTER_PARAM = "submitter";
	public static final String FIXED_FIELD_VALUE_PARAM = "values";
	public static final String LOCALE_ID_PARAM = "surveyedLocale";
	public static final String DURATION_PARAM = "duration";

	public static final String SAVE_SURVEY_INSTANCE_ACTION = "saveSurveyInstance";
	public static final String RESET_SURVEY_INSTANCE_ACTION = "resetSurveyInstance";
	public static final String SAVE_FIXED_FIELD_SURVEY_INSTANCE_ACTION = "ingestFixedFormat";
	public static final String UPDATE_SUMMARIES_ACTION = "updateSummaries";
	public static final String SAVE_MESSAGE_ACTION = "saveMessage";

	public static final String FIELD_VAL_DELIMITER = ";;";

	private Long surveyId;
	private Long surveyedLocaleId;
	private Long surveyInstanceId = null;
	private Long duration = null;
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
			String[] vals = URLDecoder.decode(req.getParameter(FIXED_FIELD_VALUE_PARAM), "UTF-8").split(
					FIELD_VAL_DELIMITER);
			for (int i = 0; i < vals.length; i++) {
				fixedFieldValues.add(vals[i]);
			}
		}
		if (req.getParameter(QUESTION_ID_PARAM) != null) {
			String[] answers = req.getParameterValues(QUESTION_ID_PARAM);
			if (answers != null) {
				for (int i = 0; i < answers.length; i++) {
					String[] parts = URLDecoder.decode(answers[i], "UTF-8").split("\\|");
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
			String colDate = req.getParameter(COLLECTION_DATE_PARAM).trim();
			if (colDate.contains("%") || colDate.contains("+")) {
				colDate = URLDecoder.decode(colDate, "UTF-8");
			}
			collectionDate = IN_FMT.get().parse(colDate);
		}
		if (req.getParameter(SUBMITTER_PARAM) != null) {
			setSubmitter(URLDecoder.decode(
					req.getParameter(SUBMITTER_PARAM), "UTF-8"));
		}
		if (req.getParameter(DURATION_PARAM) != null) {
			Double duration = Double.valueOf(req.getParameter(DURATION_PARAM));
			setSurveyDuration(duration.longValue());
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
	
	public void setSurveyDuration(Long duration) {
		this.duration = duration;
	}
	
	public Long getSurveyDuration() {
		return duration;
	}

}
