package org.waterforpeople.mapping.app.web.client.dto;

import java.util.HashMap;


public class MappingSpreadsheetDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6735894528258688636L;
	private Long keyId;
	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	private String spreadsheetURL;
	private SPREADSHEET_SOURCE_TYPE spreadsheetType;
	private String mapToObject;
	private HashMap<String, MappingSpreadsheetColumnToAttribute> columnMap;

	public String getSpreadsheetURL() {
		return spreadsheetURL;
	}

	public void setSpreadsheetURL(String spreadsheetURL) {
		this.spreadsheetURL = spreadsheetURL;
	}

	public SPREADSHEET_SOURCE_TYPE getSpreadsheetType() {
		return spreadsheetType;
	}

	public void setSpreadsheetType(SPREADSHEET_SOURCE_TYPE spreadsheetType) {
		this.spreadsheetType = spreadsheetType;
	}

	public String getMapToObject() {
		return mapToObject;
	}

	public void setMapToObject(String mapToObject) {
		this.mapToObject = mapToObject;
	}

	public HashMap<String, MappingSpreadsheetColumnToAttribute> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(
			HashMap<String, MappingSpreadsheetColumnToAttribute> columnMap) {
		this.columnMap = columnMap;
	}

	public void addColumnToMap(MappingSpreadsheetColumnToAttribute mapAttribute) {
		if (columnMap == null) {
			columnMap = new HashMap<String, MappingSpreadsheetColumnToAttribute>();
		}
		columnMap.put(mapAttribute.getSpreadsheetColumn(), mapAttribute);
	}

	public enum SPREADSHEET_SOURCE_TYPE {
		GOOGLE_SPREADSHEET, EXCEL_SPREADSHEET
	}
}
