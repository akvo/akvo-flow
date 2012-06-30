/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.client.device;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DeviceDto extends BaseDto implements Comparable<DeviceDto> {

	private static final long serialVersionUID = 3197857074399585732L;
	private String phoneNumber;
	private String esn;
	private Double lastKnownLat;
	private Double lastKnownLon;
	private Double lastKnownAccuracy;
	private Date lastPositionDate;
	private String deviceGroup;
	private String deviceIdentifier;

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

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

	@Override
	public int compareTo(DeviceDto o) {
		String myId = getDisplayValue(this);
		String otherId = getDisplayValue(o);
		return myId.compareTo(otherId);
	}

	private String getDisplayValue(DeviceDto o) {
		String val = o.getDeviceIdentifier();
		if (val == null) {
			val = o.getPhoneNumber();
		}else if (o.getPhoneNumber()!=null){
			val += " ("+o.getPhoneNumber()+")";
		}
		if (val == null) {
			val = o.getEsn();
		}
		if (val == null) {
			val = "";
		}
		return val;
	}

	public String getDisplayValue() {
		return getDisplayValue(this);
	}
}
