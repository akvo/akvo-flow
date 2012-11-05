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

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.google.gwt.user.client.ui.Grid;

/**
 * classes that implement this interface should know how to bind data to a data
 * table. This includes providing the header rows and setting the field values
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataTableBinder<T extends BaseDto> {

	/**
	 * returns a list of headers to use in the table
	 * 
	 * @return
	 */
	public DataTableHeader[] getHeaders();

	/**
	 * binds an item to the grid by populating the widgets
	 * 
	 * @param grid
	 * @param row
	 */
	public void bindRow(Grid grid, T item, int row);

	/**
	 * returns the page size to use for the table
	 * 
	 * @return
	 */
	public Integer getPageSize();

}
