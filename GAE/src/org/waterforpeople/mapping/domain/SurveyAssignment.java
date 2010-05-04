package org.waterforpeople.mapping.domain;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * domain to store assignment of surveys to devices
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class SurveyAssignment extends BaseDomain {

	private static final long serialVersionUID = -2028880542041242779L;
	private List<Long> surveyIds;
	private List<Long> deviceIds;

	public List<Long> getSurveyIds() {
		return surveyIds;
	}

	public void setSurveyIds(List<Long> surveyIds) {
		this.surveyIds = surveyIds;
	}

	public List<Long> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<Long> deviceIds) {
		this.deviceIds = deviceIds;
	}

}
