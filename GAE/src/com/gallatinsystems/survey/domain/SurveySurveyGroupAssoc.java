package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseAssocDomain;

@PersistenceCapable
public class SurveySurveyGroupAssoc extends BaseAssocDomain {

	private Long surveyContainerId;
	private Long surveyGroupId;

	public Long getSurveyContainerId() {
		return surveyContainerId;
	}

	public void setSurveyContainerId(Long surveyContainerId) {
		this.surveyContainerId = surveyContainerId;
	}

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}
}
