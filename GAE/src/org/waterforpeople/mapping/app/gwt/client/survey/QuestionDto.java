package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class QuestionDto extends BaseDto implements NamedObject {

	private static final long serialVersionUID = -4708385830894435407L;

	private String text;

	private QuestionType type;
	private OptionContainerDto optionContainerDto = null;
	private List<QuestionHelpDto> questionHelpList;
	private String tip = null;	
	private Boolean mandatoryFlag = null;
	private QuestionDependencyDto questionDependency = null;
	private Long surveyId;
	private Long questionGroupId;
	private Boolean collapseable;
	private Boolean immutable;
	private Map<String, TranslationDto> translationMap;
	private String path;
	private Integer order;
	private Boolean allowMultipleFlag = null;
	private Boolean allowOtherFlag = null;
	private Boolean allowDecimal;
	private Boolean allowSign;
	private Double minVal;
	private Double maxVal;
	private Boolean isName;

	public Boolean getAllowDecimal() {
		return allowDecimal;
	}

	public void setAllowDecimal(Boolean allowDecimal) {
		this.allowDecimal = allowDecimal;
	}

	public Boolean getAllowSign() {
		return allowSign;
	}

	public void setAllowSign(Boolean allowSign) {
		this.allowSign = allowSign;
	}

	public Double getMinVal() {
		return minVal;
	}

	public void setMinVal(Double minVal) {
		this.minVal = minVal;
	}

	public Double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Double maxVal) {
		this.maxVal = maxVal;
	}

	public Boolean getIsName() {
		return isName;
	}

	public void setIsName(Boolean isName) {
		this.isName = isName;
	}

	public String getPath() {
		return path;
	}

	public String getQuestionTypeString() {
		return type.toString();
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Map<String, TranslationDto> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(Map<String, TranslationDto> translationMap) {
		this.translationMap = translationMap;
	}

	/**
	 * adds the translation to the translation map. If a translation already
	 * exists (based on language code), it will be replaced
	 * 
	 * @param trans
	 */
	public void addTranslation(TranslationDto trans) {
		if (translationMap == null) {
			translationMap = new TreeMap<String, TranslationDto>();
		}
		translationMap.put(trans.getLangCode(), trans);
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

	public List<QuestionHelpDto> getQuestionHelpList() {
		return questionHelpList;
	}

	public void setQuestionHelpList(List<QuestionHelpDto> questionHelpList) {
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
		FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, NAME, STRENGTH, DATE
	}

	@Override
	public String getDisplayName() {
		return getText();
	}

	public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
		this.allowMultipleFlag = allowMultipleFlag;
	}

	public Boolean getAllowMultipleFlag() {
		return allowMultipleFlag;
	}

	public void setAllowOtherFlag(Boolean allowOtherFlag) {
		this.allowOtherFlag = allowOtherFlag;
	}

	public Boolean getAllowOtherFlag() {
		return allowOtherFlag;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof QuestionDto)) {
			return false;
		}
		QuestionDto otherQ = (QuestionDto) other;
		if (getKeyId() != null && otherQ.getKeyId().equals(getKeyId())) {
			return true;
		} else if (getKeyId() == null && otherQ.getKeyId() == null) {
			if (getText() != null && getText().equals(otherQ.getText())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getKeyId() != null) {
			return getKeyId().hashCode();
		} else if (getText() != null) {
			return getText().hashCode();
		} else {
			return 0;
		}
	}

	public void setCollapseable(Boolean collapseable) {
		this.collapseable = collapseable;
	}

	public Boolean getCollapseable() {
		return collapseable;
	}

	public void setImmutable(Boolean immutable) {
		this.immutable = immutable;
	}

	public Boolean getImmutable() {
		return immutable;
	}
}
