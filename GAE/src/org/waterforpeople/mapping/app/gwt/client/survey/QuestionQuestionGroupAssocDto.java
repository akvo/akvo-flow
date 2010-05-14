package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;



public class QuestionQuestionGroupAssocDto extends BaseDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7578071601713731890L;
	private QuestionDto question;
	private QuestionGroupDto questionGroup;
	public QuestionDto getQuestion() {
		return question;
	}
	public void setQuestion(QuestionDto question) {
		this.question = question;
	}
	public QuestionGroupDto getQuestionGroup() {
		return questionGroup;
	}
	public void setQuestionGroup(QuestionGroupDto questionGroup) {
		this.questionGroup = questionGroup;
	}
}
