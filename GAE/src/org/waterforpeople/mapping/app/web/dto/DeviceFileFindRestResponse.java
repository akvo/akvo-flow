package org.waterforpeople.mapping.app.web.dto;

import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;

import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceFileFindRestResponse extends RestResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5516233980214584505L;
	
	private Boolean foundFlag = false;
	
	private DeviceFilesDto deviceFile = null;

	public Boolean getFoundFlag() {
		return foundFlag;
	}

	public void setFoundFlag(Boolean foundFlag) {
		this.foundFlag = foundFlag;
	}

	public DeviceFilesDto getDeviceFile() {
		return deviceFile;
	}

	public void setDeviceFile(DeviceFilesDto deviceFile) {
		this.deviceFile = deviceFile;
	}
}
