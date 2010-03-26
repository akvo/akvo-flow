package com.gallatinsystems.survey.app.web.client.dto;

public class SurveyQuestionGroupAssoc {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6593530633749910719L;
	private Survey survey;
	private QuestionGroup questionGroup;

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public QuestionGroup getQuestionGroup() {
		return questionGroup;
	}

	public void setQuestionGroup(QuestionGroup questionGroup) {
		this.questionGroup = questionGroup;
	}

}
