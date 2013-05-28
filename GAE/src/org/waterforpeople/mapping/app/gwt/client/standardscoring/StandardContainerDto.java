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

package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import java.util.ArrayList;

public class StandardContainerDto {
	public enum ControlType {TextBox, List, Date};
	public enum ValueType {String, Integer, Double, Float, Boolean, Key};
	public class Row{
		private String name=null;
		private ControlType controlType= null;
		private String value = null;
		private ValueType valueType = null;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ControlType getControlType() {
			return controlType;
		}
		public void setControlType(ControlType controlType) {
			this.controlType = controlType;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public ValueType getValueType() {
			return valueType;
		}
		public void setValueType(ValueType valueType) {
			this.valueType = valueType;
		}
		
		
	}
	
	private ArrayList<Row> rows  =  null;

	public void setRows(ArrayList<Row> rows) {
		this.rows = rows;
	}

	public ArrayList<Row> getRows() {
		return rows;
	}

}
