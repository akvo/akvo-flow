package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class QuestionGroupDto extends BaseDto implements NamedObject {

	private static final long serialVersionUID = -7253934961271624253L;

	private TreeMap<Integer, QuestionDto> questionMap = null;

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

	public void setQuestionMap(TreeMap<Integer, QuestionDto> questionMap) {
		this.questionMap = questionMap;
	}

	public TreeMap<Integer, QuestionDto> getQuestionMap() {
		return questionMap;
	}

	public void addQuestion(QuestionDto item, Integer position) {
		if (questionMap == null) {
			questionMap = new TreeMap<Integer, QuestionDto>();
			questionMap.put(position, item);
		} else {
			questionMap.put(position, item);

		}

	}

	@Override
	public String getDisplayName() {
		return getCode();
	}
}
