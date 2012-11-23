package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;

public class SurveyGroupPayload implements Serializable {

	private static final long serialVersionUID = 8812994636904876896L;
	SurveyGroupDto survey_group = null;

	public SurveyGroupDto getSurvey_group() {
		return survey_group;
	}

	public void setSurvey_group(SurveyGroupDto survey_group) {
		this.survey_group = survey_group;
	}
}
