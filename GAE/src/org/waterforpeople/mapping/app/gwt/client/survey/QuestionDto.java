package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionDto extends BaseDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4708385830894435407L;

	private String text;

	private QuestionType type;
	private ArrayList<QuestionOptionDto> optionsList;
	private ArrayList<QuestionHelpDto> questionHelpList;

	public void addOption(QuestionOptionDto questionOption) {
		if (optionsList == null) {
			optionsList = new ArrayList<QuestionOptionDto>();
		}
		optionsList.add(questionOption);
	}

	public void addQuestionHelp(QuestionHelpDto questionHelp) {
		if (questionHelpList == null) {
			questionHelpList = new ArrayList<QuestionHelpDto>();
		}
		questionHelpList.add(questionHelp);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public ArrayList<QuestionOptionDto> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(ArrayList<QuestionOptionDto> optionsList) {
		this.optionsList = optionsList;
	}

	public ArrayList<QuestionHelpDto> getQuestionHelpList() {
		return questionHelpList;
	}

	public void setQuestionHelpList(ArrayList<QuestionHelpDto> questionHelpList) {
		this.questionHelpList = questionHelpList;
	}

	public enum QuestionType {
		FREE_TEXT, OPTION, NUMBER
	}

}
