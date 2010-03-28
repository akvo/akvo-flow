package org.waterforpeople.mapping.app.gwt.client.device;

import java.io.Serializable;

public class DeviceDto implements Serializable {

	private static final long serialVersionUID = 3197857074399585732L;
	private String phoneNumber;
	private String esn;

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
