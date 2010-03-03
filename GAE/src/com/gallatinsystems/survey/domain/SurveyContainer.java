package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class SurveyContainer extends BaseDomain {
	private String description;
	private String notes;

	private com.google.appengine.api.datastore.Text surveyDocument;

	public com.google.appengine.api.datastore.Text getSurveyDocument() {
		return surveyDocument;
	}

	public void setSurveyDocument(
			com.google.appengine.api.datastore.Text surveyDocument) {
		this.surveyDocument = surveyDocument;
	}

}
