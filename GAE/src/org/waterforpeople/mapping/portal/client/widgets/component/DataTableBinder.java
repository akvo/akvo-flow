package org.waterforpeople.mapping.portal.client.widgets.component;

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

}
