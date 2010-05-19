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
	private OptionContainerDto optionContainer = null;
	private ArrayList<QuestionHelpDto> questionHelpList;
	private String tip = null;
	private String validationRule = null;

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

	public ArrayList<QuestionHelpDto> getQuestionHelpList() {
		return questionHelpList;
	}

	public void setQuestionHelpList(ArrayList<QuestionHelpDto> questionHelpList) {
		this.questionHelpList = questionHelpList;
	}

	public void setOptionContainer(OptionContainerDto optionContainer) {
		this.optionContainer = optionContainer;
	}

	public OptionContainerDto getOptionContainer() {
		return optionContainer;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getTip() {
		return tip;
	}

	public void setValidationRule(String validationRule) {
		this.validationRule = validationRule;
	}

	public String getValidationRule() {
		return validationRule;
	}

	public enum QuestionType {
		FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN
	}

}
