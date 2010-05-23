package org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper;

import java.util.ArrayList;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SpreadsheetMappingAttributeServiceAsync {

	void listSpreadsheets(AsyncCallback<ArrayList<String>> callback);

	void listSpreadsheetColumns(String spreadsheetName,
			AsyncCallback<ArrayList<String>> callback);

	void listObjectAttributes(String objectNames,
			AsyncCallback<ArrayList<String>> callback);

	void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef,
			AsyncCallback<Void> callback);

	void processSpreadsheet(MappingSpreadsheetDefinition mapDef,
			AsyncCallback<String> callback);

	void getMappingSpreadsheetDefinition(String spreadsheetName,
			AsyncCallback<MappingDefinitionColumnContainer> callback);

	void listSpreadsheetsFromFeed(String feedURL,
			AsyncCallback<ArrayList<String>> callback);

	void processSurveySpreadsheet(String spreadsheetName, int startRow,Long groupId,
			AsyncCallback<Void> callback);


}
