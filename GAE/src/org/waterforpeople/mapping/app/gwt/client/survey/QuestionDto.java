package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class QuestionDto extends BaseDto implements NamedObject {

	private static final long serialVersionUID = -4708385830894435407L;

	private String text;

	private QuestionType type;
	private OptionContainerDto optionContainerDto = null;
	private ArrayList<QuestionHelpDto> questionHelpList;
	private String tip = null;
	private String validationRule = null;
	private Boolean mandatoryFlag = null;
	private QuestionDependencyDto questionDependency = null;
	private Long surveyId;
	private Long questionGroupId;
	private TreeMap<String, TranslationDto> translationMap;

	public TreeMap<String, TranslationDto> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(TreeMap<String, TranslationDto> translationMap) {
		this.translationMap = translationMap;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(Long questionGroupId) {
		this.questionGroupId = questionGroupId;
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

	public ArrayList<QuestionHelpDto> getQuestionHelpList() {
		return questionHelpList;
	}

	public void setQuestionHelpList(ArrayList<QuestionHelpDto> questionHelpList) {
		this.questionHelpList = questionHelpList;
	}

	public void setOptionContainerDto(OptionContainerDto optionContainer) {
		this.optionContainerDto = optionContainer;
	}

	public OptionContainerDto getOptionContainerDto() {
		return optionContainerDto;
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

	public void setMandatoryFlag(Boolean mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	public Boolean getMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setQuestionDependency(QuestionDependencyDto questionDependency) {
		this.questionDependency = questionDependency;
	}

	public QuestionDependencyDto getQuestionDependency() {
		return questionDependency;
	}

	public enum QuestionType {
		FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, NAME
	}

	@Override
	public String getDisplayName() {
		return getText();
	}
}
