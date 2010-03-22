package com.gallatinsystems.survey.domain.xml;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseAssocDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyGroupAssoc extends BaseAssocDomain {	
	private static final long serialVersionUID = 404944397993129471L;
	private String surveyGroupFromCode;
	private String surveyGroupToCode;

	public String getSurveyGroupFromCode() {
		return surveyGroupFromCode;
	}

	public void setSurveyGroupFromCode(String surveyGroupFromCode) {
		this.surveyGroupFromCode = surveyGroupFromCode;
	}

	public String getSurveyGroupToCode() {
		return surveyGroupToCode;
	}

	public void setSurveyGroupToCode(String surveyGroupToCode) {
		this.surveyGroupToCode = surveyGroupToCode;
	}

}
