package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;


public class SurveyQuestionGroupAssocDto extends BaseDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6593530633749910719L;
	private SurveyDto survey;
	private QuestionGroupDto questionGroup;

	public SurveyDto getSurvey() {
		return survey;
	}

	public void setSurvey(SurveyDto survey) {
		this.survey = survey;
	}

	public QuestionGroupDto getQuestionGroup() {
		return questionGroup;
	}

	public void setQuestionGroup(QuestionGroupDto questionGroup) {
		this.questionGroup = questionGroup;
	}

}
