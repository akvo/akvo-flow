package org.waterforpeople.mapping.dataexport.service;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.web.dto.AccessPointResponse;
import org.waterforpeople.mapping.dataexport.AccessPointExporter;

public class RestAccessPointParser {
	public static List<AccessPointDto> parseAccessPoint(String response)
			throws Exception {
		AccessPointExporter ape = new AccessPointExporter();
		AccessPointResponse apr = ape.parseJson(response);
		return apr.getAccessPointDto();
	}

}
