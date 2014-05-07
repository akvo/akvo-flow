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

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Handles requests to the DataProcessing Rest Service
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataProcessorRequest extends RestRequest {
	private static final long serialVersionUID = -4553663867954174523L;
	public static final String PROJECT_FLAG_UPDATE_ACTION = "projectFlagUpdate";
	public static final String REBUILD_QUESTION_SUMMARY_ACTION = "rebuildQuestionSummary";
	public static final String IMPORT_REMOTE_SURVEY_ACTION = "importRemoteSurvey";
	public static final String COPY_SURVEY = "copySurvey";
	public static final String FIX_OPTIONS2VALUES_ACTION = "fixOptions2Values";
	public static final String FIX_NULL_SUBMITTER_ACTION = "fixNullSubmitter";
	public static final String FIX_DUPLICATE_OTHER_TEXT_ACTION = "fixDuplicateOtherText";
	public static final String DELETE_DUPLICATE_QAS = "deleteDuplicatedQAS";
	public static final String RECOMPUTE_LOCALE_CLUSTERS = "recomputeLocaleClusters";
	public static final String SURVEY_INSTANCE_SUMMARIZER = "surveyInstanceSummarizer";
	public static final String TRIM_OPTIONS = "trimOptions";
	public static final String RESCORE_AP_ACTION = "rescoreAp";
	public static final String SOURCE_PARAM = "source";
	public static final String COUNTRY_PARAM = "country";
	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String SURVEY_INSTANCE_PARAM = "surveyInstanceId";
	public static final String QAS_ID_PARAM = "qasId";
	public static final String DELTA_PARAM = "delta";
	public static final String API_KEY_PARAM = "apiKey";
	public static final String OFFSET_PARAM = "offset";
	public static final String CHANGE_LOCALE_TYPE_ACTION = "changeLocaleType";
	public static final String ADD_CREATION_SURVEY_ID_TO_LOCALE = "addCreationSurveyIdToLocale";
	public static final String ADD_TRANSLATION_FIELDS = "addTranslationFields";
	public static final String RECREATE_LOCALES = "recreateLocales";

	private String country;
	private String source;
	private Long surveyId;
	private Long surveyInstanceId;
	private Long qasId;
	private Integer delta;
	private String apiKey;
	private Long offset = 0L;

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		country = req.getParameter(COUNTRY_PARAM);
		source = req.getParameter(SOURCE_PARAM);
		if (req.getParameter(SURVEY_ID_PARAM) != null) {
			try {
				surveyId = new Long(req.getParameter(SURVEY_ID_PARAM).trim());
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
								+ " must be an integer"));
			}
		}
		if (req.getParameter(SURVEY_INSTANCE_PARAM) != null) {
			try {
				setSurveyInstanceId(new Long(req.getParameter(SURVEY_INSTANCE_PARAM).trim()));
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, SURVEY_INSTANCE_PARAM
								+ " must be an integer"));
			}
		}
		if (req.getParameter(QAS_ID_PARAM) != null) {
			try {
				setQasId(new Long(req.getParameter(QAS_ID_PARAM).trim()));
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, QAS_ID_PARAM
								+ " must be an integer"));
			}
		}
		
		if (req.getParameter(DELTA_PARAM) != null) {
			try {
				setDelta(new Integer(req.getParameter(DELTA_PARAM).trim()));
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE, DELTA_PARAM
								+ " must be an integer"));
			}
		}

		if (req.getParameter(API_KEY_PARAM) != null) {
			setApiKey(req.getParameter(API_KEY_PARAM).trim());
		}

		if (req.getParameter(OFFSET_PARAM) != null) {
			setOffset(Long.valueOf(req.getParameter(OFFSET_PARAM).trim()));
		}
	}

	@Override
	protected void populateErrors() {
		// no-op

	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public Long getQasId() {
		return qasId;
	}

	public void setQasId(Long qasId) {
		this.qasId = qasId;
	}

	public Integer getDelta() {
		return delta;
	}

	public void setDelta(Integer delta) {
		this.delta = delta;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}
}
