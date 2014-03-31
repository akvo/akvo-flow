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
	public static final String LIST_INSTANCE_RESPONSE_ACTION = "listInstanceResponse";
	public static final String LIST_QUESTION_RESPONSE_ACTION = "listQuestionResponse";

	public static final String SURVEY_ID_PARAM = "surveyId";
	public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
	public static final String QUESTION_ID_PARAM = "questionId";
	public static final String COUNTRY_PARAM = "countryCode";
	public static final String DATE_PARAM = "date";
	public static final String INCLUDE_DATE_PARAM = "includeDate";
	public static final String LAST_COLLECTION_PARAM = "lastCollection";

	private static final ThreadLocal<DateFormat> inFmt = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		};
	};

	private Long surveyId;
	private Long surveyInstanceId;
	private Long questionId;
	private String countryCode;
	private Date date;
	private boolean includeDate;
	private boolean lastCollection = false;

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

	public boolean includeDate() {
		return includeDate;
	}

	public void setLastCollection(boolean lastCollection) {
		this.lastCollection = lastCollection;
	}

	public boolean getLastCollection() {
		return lastCollection;
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
			questionId = Long.parseLong(req.getParameter(QUESTION_ID_PARAM)
					.trim());
		}
		String instanceId = req.getParameter(SURVEY_INSTANCE_ID_PARAM);
		if (instanceId!= null && instanceId.trim().length()>0) {
			surveyInstanceId = Long.parseLong(instanceId.trim());
		}
		if (req.getParameter(DATE_PARAM) != null) {
			date = inFmt.get().parse(req.getParameter(DATE_PARAM));
		}
		if (req.getParameter(INCLUDE_DATE_PARAM) != null) {
			includeDate = Boolean.parseBoolean(req
					.getParameter(INCLUDE_DATE_PARAM));
		} else {
			includeDate = false;
		}

		lastCollection = req.getParameter(LAST_COLLECTION_PARAM) != null
				&& "true".equals(req.getParameter(LAST_COLLECTION_PARAM));
		countryCode = req.getParameter(COUNTRY_PARAM);
	}

}
