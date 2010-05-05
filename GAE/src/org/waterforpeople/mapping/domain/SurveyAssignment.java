package org.waterforpeople.mapping.domain;

import java.util.Date;
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
	private String name;
	private String language;
	private Date startDate;
	private Date endDate;

	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

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
