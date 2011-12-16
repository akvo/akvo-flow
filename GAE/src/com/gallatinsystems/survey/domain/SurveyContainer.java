package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * 
 * Wrapper object allowing persistence of xml representations of surveys
 *
 */
@PersistenceCapable
public class SurveyContainer extends BaseDomain {
	
	private static final long serialVersionUID = -1445653380398913451L;
	private Long surveyId = null;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getSurveyId() {
		return surveyId;
	}

}
