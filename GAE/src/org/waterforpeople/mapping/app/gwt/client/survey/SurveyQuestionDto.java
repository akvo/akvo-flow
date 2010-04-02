package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;

public class SurveyQuestionDto implements Serializable{

	private static final long serialVersionUID = -7205581615394240179L;
	private String questionId;
	private String questionText;
	private String questionType;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

}