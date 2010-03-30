package org.waterforpeople.mapping.app.web.client;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetDefinition;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SpreadsheetMappingAttributeService extends RemoteService {
	public ArrayList<String> listSpreadsheets();

	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName);

	public ArrayList<String> listObjectAttributes(String objectNames);

	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef);
	public void processSpreadsheet(MappingSpreadsheetDefinition mapDef);
}
