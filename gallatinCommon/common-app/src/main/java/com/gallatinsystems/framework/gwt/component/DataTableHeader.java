package com.gallatinsystems.framework.gwt.component;

/**
 * object representing header rows for a data table
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataTableHeader {

	private String displayName;
	private String fieldName;
	private boolean sortable;

	public DataTableHeader(String name) {
		displayName = name;
	}

	public DataTableHeader(String name, String key, boolean sortable) {
		displayName = name;
		if (key == null) {
			fieldName = displayName;
		} else {
			fieldName = key;
		}
		this.sortable = sortable;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public String toString() {
		return displayName;
	}

}
