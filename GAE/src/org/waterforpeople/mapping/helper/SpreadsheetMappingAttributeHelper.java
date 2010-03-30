package org.waterforpeople.mapping.helper;

import java.io.IOException;
import java.util.ArrayList;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition;

import com.google.gdata.util.ServiceException;

public class SpreadsheetMappingAttributeHelper {

	public MappingSpreadsheetDefinition saveSpreadsheetMappingAttribute(
			MappingSpreadsheetDefinition mapDef) {
		return null;
	}

	public ArrayList<String> processSpreadsheet(
			MappingSpreadsheetDefinition mapDef) {
		String spreadsheetName = mapDef.getSpreadsheetURL();
		if (!spreadsheetName.trim().isEmpty()) {
			SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter();
			sapa.processSpreadsheetOfAccessPoints(spreadsheetName);
			// ToDo: need to decide how to let them know this is finished since
			// it could take a while
			// need to think about how to manage errors as well.
		}

		return null;
	}

	public ArrayList<String> listObjectAttributes(String objectNames) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName) throws IOException, ServiceException {
		if (!spreadsheetName.trim().isEmpty()) {
			SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter();
			return sapa.listColumns(spreadsheetName);
		}
		return null;
	}

	public ArrayList<String> listSpreadsheets() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef) {

	}
}
