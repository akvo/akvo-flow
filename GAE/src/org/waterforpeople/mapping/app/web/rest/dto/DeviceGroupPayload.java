package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceGroupDto;

public class DeviceGroupPayload implements Serializable {

	private static final long serialVersionUID = -6251221364953360051L;
	DeviceGroupDto device_group = null;

	public DeviceGroupDto getDevice_group() {
		return device_group;
	}

	public void setDevice_group(DeviceGroupDto device_group) {
		this.device_group = device_group;
	}
}
