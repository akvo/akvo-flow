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

/**
 * This interface is to be used with the PaginatedDataTable. It defines
 * callbacks that will be fired when the user interacts with the data grid (thus
 * allowing the owning component to respond by displaying a detail view or
 * fetching more data from the server).
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataTableListener<T extends BaseDto> {

	/**
	 * called when the user clicks on a table row
	 * 
	 * @param item
	 */
	public void onItemSelected(T item);

	/**
	 * called when the user clicks next/previous or when the data is sorted
	 * 
	 * @param cursor
	 */
	public void requestData(String cursor, boolean isResort);

}
