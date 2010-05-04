package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

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
