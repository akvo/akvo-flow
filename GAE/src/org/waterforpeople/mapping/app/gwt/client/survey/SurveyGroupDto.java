package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class SurveyGroupDto extends BaseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2235565143615667202L;

	private String description;
	private String code;
	private Date createdDateTime;
	private Date lastUpdateDateTime;

	private ArrayList<SurveyDto> surveyList = null;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}

	public void setLastUpdateDateTime(Date lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}

	public void setSurveyList(ArrayList<SurveyDto> surveyList) {
		this.surveyList = surveyList;
	}

	public ArrayList<SurveyDto> getSurveyList() {
		return surveyList;
	}

	public void addSurvey(SurveyDto item) {
		if (surveyList == null)
			surveyList = new ArrayList<SurveyDto>();
		else
			surveyList.add(item);
	}

}
