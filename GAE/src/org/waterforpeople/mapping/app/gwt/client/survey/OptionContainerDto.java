package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;
import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class OptionContainerDto extends BaseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4002853404925002791L;
	private ArrayList<QuestionOptionDto> optionsList = null;
	private Boolean allowMultipleFlag = null;

	public Boolean getAllowOtherFlag() {
		return allowOtherFlag;
	}

	public void setAllowOtherFlag(Boolean allowOtherFlag) {
		this.allowOtherFlag = allowOtherFlag;
	}

	public void setOptionsList(ArrayList<QuestionOptionDto> optionsList) {
		this.optionsList = optionsList;
	}

	public ArrayList<QuestionOptionDto> getOptionsList() {
		return optionsList;
	}

	private Boolean allowOtherFlag = null;

	public void addQuestionOption(QuestionOptionDto questionOption) {
		if (optionsList == null)
			optionsList = new ArrayList<QuestionOptionDto>();
		optionsList.add(questionOption);
	}

	public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
		this.allowMultipleFlag = allowMultipleFlag;
	}

	public Boolean getAllowMultipleFlag() {
		return allowMultipleFlag;
	}
}
