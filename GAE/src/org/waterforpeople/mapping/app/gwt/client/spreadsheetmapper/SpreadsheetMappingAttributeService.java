package org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper;

import java.util.ArrayList;


import com.google.gwt.user.client.rpc.RemoteService;

public interface SpreadsheetMappingAttributeService extends RemoteService {
	public ArrayList<String> listSpreadsheets();

	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName);

	public ArrayList<String> listObjectAttributes(String objectNames);

	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef);
	public String processSpreadsheet(MappingSpreadsheetDefinition mapDef);
	
	public MappingDefinitionColumnContainer getMappingSpreadsheetDefinition(String spreadsheetName);

	public void processSurveySpreadsheet(String spreadsheetName);
	

	ArrayList<String> listSpreadsheetsFromFeed(String feedURL);
}
