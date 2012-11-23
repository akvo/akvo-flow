package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

public class SurveyPayload implements Serializable {

	private static final long serialVersionUID = 1927125743629112187L;
	SurveyDto survey = null;

	public SurveyDto getSurvey() {
		return survey;
	}

	public void setSurvey(SurveyDto survey) {
		this.survey = survey;
	}
}
