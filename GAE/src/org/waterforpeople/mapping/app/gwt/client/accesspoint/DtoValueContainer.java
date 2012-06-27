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

package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DtoValueContainer extends BaseDto {
	private static final long serialVersionUID = -5214719338768195921L;
	private ArrayList<Row> rows = new ArrayList<Row>();
	
	

	public void addRow(String fieldName, String fieldDisplayName,
			Integer order, String fieldType, String fieldValue) {
		Row row = new Row(fieldName, fieldDisplayName, order, fieldType,
				fieldValue);
		getRows().add(row);
	}



	public void setRows(ArrayList<Row> rows) {
		this.rows = rows;
	}



	public ArrayList<Row> getRows() {
		return rows;
	}
}
