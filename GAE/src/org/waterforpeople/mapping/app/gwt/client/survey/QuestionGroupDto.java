package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.HashMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionGroupDto extends BaseDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7253934961271624253L;
	/**
	 * 
	 */
	private HashMap<Integer, QuestionDto> questionMap = null;

	private String code;
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setQuestionMap(HashMap<Integer, QuestionDto> questionMap) {
		this.questionMap = questionMap;
	}

	public HashMap<Integer, QuestionDto> getQuestionMap() {
		return questionMap;
	}

	public void addQuestion(QuestionDto item, Integer position) {
		if (questionMap == null) {
			questionMap = new HashMap<Integer, QuestionDto>();
			questionMap.put(position, item);
		} else {
			questionMap.put(position, item);

		}

	}
}
