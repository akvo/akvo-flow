package com.gallatinsystems.survey.app.web.client.dto;


public class QuestionQuestionGroupAssoc{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7578071601713731890L;
	private Question question;
	private QuestionGroup questionGroup;
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public QuestionGroup getQuestionGroup() {
		return questionGroup;
	}
	public void setQuestionGroup(QuestionGroup questionGroup) {
		this.questionGroup = questionGroup;
	}
}
