package org.waterforpeople.mapping.app.gwt.client.device;

import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

public class DeviceDto extends BaseDto {

	private static final long serialVersionUID = 3197857074399585732L;
	private String phoneNumber;
	private String esn;
	private Double lastKnownLat;
	private Double lastKnownLon;
	private Double lastKnownAccuracy;
	private Date lastPositionDate;
	private String deviceGroup;

	public String getDeviceGroup() {
		return deviceGroup;
	}

	public void setDeviceGroup(String deviceGroup) {
		this.deviceGroup = deviceGroup;
	}

	public Double getLastKnownLat() {
		return lastKnownLat;
	}

	public void setLastKnownLat(Double lastKnownLat) {
		this.lastKnownLat = lastKnownLat;
	}

	public Double getLastKnownLon() {
		return lastKnownLon;
	}

	public void setLastKnownLon(Double lastKnownLon) {
		this.lastKnownLon = lastKnownLon;
	}

	public Double getLastKnownAccuracy() {
		return lastKnownAccuracy;
	}

	public void setLastKnownAccuracy(Double lastKnownAccuracy) {
		this.lastKnownAccuracy = lastKnownAccuracy;
	}

	public Date getLastPositionDate() {
		return lastPositionDate;
	}

	public void setLastPositionDate(Date lastPositionDate) {
		this.lastPositionDate = lastPositionDate;
	}

	public String getEsn() {
		return esn;
	}

	public void setEsn(String esn) {
		this.esn = esn;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
