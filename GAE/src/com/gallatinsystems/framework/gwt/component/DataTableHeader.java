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
