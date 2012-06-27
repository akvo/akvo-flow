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

package org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper;

import java.util.ArrayList;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("spreadsheetattributemapperrpc")
public interface SpreadsheetMappingAttributeService extends RemoteService {
	public ArrayList<String> listSpreadsheets() throws Exception;

	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName);

	public ArrayList<String> listObjectAttributes(String objectNames);

	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef);
	public String processSpreadsheet(MappingSpreadsheetDefinition mapDef);
	
	public MappingDefinitionColumnContainer getMappingSpreadsheetDefinition(String spreadsheetName);

	public void processSurveySpreadsheet(String spreadsheetName, int startRow, Long groupId);
	

	ArrayList<String> listSpreadsheetsFromFeed(String feedURL) throws Exception;
}
