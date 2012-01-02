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
