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

import java.io.Serializable;
import java.util.ArrayList;

public class MappingSpreadsheetDefinition implements Serializable {
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
