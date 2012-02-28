package org.waterforpeople.mapping.app.web.dto;

import java.util.Date;

import com.gallatinsystems.framework.rest.RestResponse;

public class SurveyInstanceResponse extends RestResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 345987367971347826L;
	
	private Long surveyInstanceId = null;
	private Date createdDateTime = null;
	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}
	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
}
