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

package org.waterforpeople.mapping.app.gwt.server.displaytemplate;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateManagerService;
import org.waterforpeople.mapping.app.gwt.client.displaytemplate.MapBalloonDefinitionDto;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DisplayTemplateMappingServiceImpl extends RemoteServiceServlet
		implements DisplayTemplateManagerService {

	private static final long serialVersionUID = -4261694426321368183L;

	@Override
	public ArrayList<String> getLabels() {
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("Order");
		labels.add("Description");
		labels.add("Attribute");
		return null;
	}

	@Override
	public void delete(Long keyId) {
		// TODO: delete
	}

	@Override
	public ArrayList<MapBalloonDefinitionDto> getRows() {
		return null;
	}

	@Override
	public ArrayList<String> listObjectAttributes(String objectNames) {
		return SpreadsheetMappingAttributeHelper.listObjectAttributes();
	}

	@Override
	public MapBalloonDefinitionDto save(MapBalloonDefinitionDto item) {
		return null;
	}

}
