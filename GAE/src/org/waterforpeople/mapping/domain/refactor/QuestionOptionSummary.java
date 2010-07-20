package org.waterforpeople.mapping.domain.refactor;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public class QuestionOptionSummary extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4569158260741601484L;
	private Key surveyKey = null;
	private String surveyCode = null;
	private Key quesitonGroupKey = null;
	private String questionGroupCode = null;
	private Text questionOptionText = null;
	public Key getSurveyKey() {
		return surveyKey;
	}
	public void setSurveyKey(Key surveyKey) {
		this.surveyKey = surveyKey;
	}
	public String getSurveyCode() {
		return surveyCode;
	}
	public void setSurveyCode(String surveyCode) {
		this.surveyCode = surveyCode;
	}
	public Key getQuesitonGroupKey() {
		return quesitonGroupKey;
	}
	public void setQuesitonGroupKey(Key quesitonGroupKey) {
		this.quesitonGroupKey = quesitonGroupKey;
	}
	public String getQuestionGroupCode() {
		return questionGroupCode;
	}
	public void setQuestionGroupCode(String questionGroupCode) {
		this.questionGroupCode = questionGroupCode;
	}
	public Text getQuestionOptionText() {
		return questionOptionText;
	}
	public void setQuestionOptionText(Text questionOptionText) {
		this.questionOptionText = questionOptionText;
	}
	
	
}
