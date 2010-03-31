package org.waterforpeople.mapping.domain;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MappingSpreadsheetDefinition extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6735894528258688636L;

	private String spreadsheetURL;
	private SPREADSHEET_SOURCE_TYPE spreadsheetType;
	private String mapToObject;
	@Persistent(serialized = "true")
	private ArrayList<MappingSpreadsheetColumnToAttribute> columnMap;

	public ArrayList<MappingSpreadsheetColumnToAttribute> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(
			ArrayList<MappingSpreadsheetColumnToAttribute> columnMap) {
		this.columnMap = columnMap;
	}

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

	public void addColumnToMap(MappingSpreadsheetColumnToAttribute mapAttribute) {
		if (columnMap == null) {
			columnMap = new ArrayList<MappingSpreadsheetColumnToAttribute>();
		}
		columnMap.add(mapAttribute);
	}

	public enum SPREADSHEET_SOURCE_TYPE {
		GOOGLE_SPREADSHEET, EXCEL_SPREADSHEET
	}

}
