package org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper;

import java.io.Serializable;
import java.util.ArrayList;

public class MappingDefinitionColumnContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4809454074296756969L;
	private MappingSpreadsheetDefinition mapDef;
	private ArrayList<String> spreadsheetColsList;
	public MappingSpreadsheetDefinition getMapDef() {
		return mapDef;
	}
	public void setMapDef(MappingSpreadsheetDefinition mapDef) {
		this.mapDef = mapDef;
	}
	public ArrayList<String> getSpreadsheetColsList() {
		return spreadsheetColsList;
	}
	public void setSpreadsheetColsList(ArrayList<String> spreadsheetColsList) {
		this.spreadsheetColsList = spreadsheetColsList;
	}
}
