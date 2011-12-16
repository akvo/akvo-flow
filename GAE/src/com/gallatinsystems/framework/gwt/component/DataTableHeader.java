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

	/**
	 * creates a non-sortable data table header
	 * 
	 * @param name
	 */
	public DataTableHeader(String name) {
		displayName = name;
	}

	/**
	 * defines a header with the name and key specified
	 * 
	 * @param name
	 *            - name to display
	 * @param key
	 *            - name of field within the bound object (used for sorting)
	 * @param sortable
	 *            - flag indicating whether or not the table can be sorted by
	 *            this field
	 */
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
