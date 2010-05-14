package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;


public class SurveySurveyGroupAssocDto extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3225985948073456482L;
	private Long surveyContainerId;
	private Long surveyGroupId;

	public Long getSurveyContainerId() {
		return surveyContainerId;
	}

	public void setSurveyContainerId(Long surveyContainerId) {
		this.surveyContainerId = surveyContainerId;
	}

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}
}
