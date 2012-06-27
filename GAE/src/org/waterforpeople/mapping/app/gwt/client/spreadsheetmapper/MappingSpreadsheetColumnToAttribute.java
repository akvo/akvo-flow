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

public class MappingSpreadsheetColumnToAttribute implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2232937366226902644L;
	private Long keyId;

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	private String spreadsheetColumn;
	private String objectAttribute;
	private String formattingRule;

	public String getSpreadsheetColumn() {
		return spreadsheetColumn;
	}

	public void setSpreadsheetColumn(String spreadsheetColumn) {
		this.spreadsheetColumn = spreadsheetColumn;
	}

	public String getObjectAttribute() {
		return objectAttribute;
	}

	public void setObjectAttribute(String objectAttribute) {
		this.objectAttribute = objectAttribute;
	}

	public String getFormattingRule() {
		return formattingRule;
	}

	public void setFormattingRule(String formattingRule) {
		this.formattingRule = formattingRule;
	}

}
