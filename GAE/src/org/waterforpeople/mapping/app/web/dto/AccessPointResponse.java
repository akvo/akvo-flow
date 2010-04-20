package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;

import com.gallatinsystems.framework.rest.RestResponse;

/**
 * data structure for responses from the AccessPoint rest service
 * 
 * @author Christopher Fagiani
 */
public class AccessPointResponse extends RestResponse {

	private static final long serialVersionUID = 5595956082396303102L;

	public List<AccessPointDto> getAccessPointDto() {
		return accessPointDto;
	}

	public void setAccessPointDto(List<AccessPointDto> accessPointDto) {
		this.accessPointDto = accessPointDto;
	}

	private List<AccessPointDto> accessPointDto;
}