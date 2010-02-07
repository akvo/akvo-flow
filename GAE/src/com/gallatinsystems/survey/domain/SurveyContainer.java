package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyContainer {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	private com.google.appengine.api.datastore.Text surveyDocument;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public com.google.appengine.api.datastore.Text getSurveyDocument() {
		return surveyDocument;
	}

	public void setSurveyDocument(
			com.google.appengine.api.datastore.Text surveyDocument) {
		this.surveyDocument = surveyDocument;
	}

	
}
