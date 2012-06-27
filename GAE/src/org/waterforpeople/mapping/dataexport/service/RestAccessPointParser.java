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
