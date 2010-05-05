package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

/**
 * dto to transfer survey/device assignments
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentDto extends BaseDto {

	private static final long serialVersionUID = -5023203087864769109L;
	private ArrayList<SurveyDto> surveys;
	private ArrayList<DeviceDto> devices;
	private Date startDate;
	private Date endDate;
	private String name;
	private String language;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SurveyDto> getSurveys() {
		return surveys;
	}

	public void setSurveys(ArrayList<SurveyDto> surveys) {
		this.surveys = surveys;
	}

	public ArrayList<DeviceDto> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<DeviceDto> devices) {
		this.devices = devices;
	}

}
