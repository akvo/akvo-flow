package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class SurveyDto extends BaseDto implements NamedObject {
	private static final long serialVersionUID = 6593732844403807030L;
	private String name;
	private String version;
	private String description;
	private String status;
	private ArrayList<QuestionGroupDto> questionGroupList;
	
	public void addQuestionGroup(QuestionGroupDto questionGroup) {
		if (questionGroupList == null) {
			questionGroupList = new ArrayList<QuestionGroupDto>();
		}
		questionGroupList.add(questionGroup);
	}

	public ArrayList<QuestionGroupDto> getQuestionGroupList() {
		return questionGroupList;
	}

	public void setQuestionGroupList(ArrayList<QuestionGroupDto> questionGroupList) {
		this.questionGroupList = questionGroupList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getDisplayName() {
		String display = name;
		if (display == null || display.trim().length() == 0) {
			display = getKeyId().toString();
		}
		display = display + " - v." + getVersion();
		return display;		
	}
}
