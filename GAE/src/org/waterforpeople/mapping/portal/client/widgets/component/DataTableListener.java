package org.waterforpeople.mapping.portal.client.widgets.component;

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
	 * called when the user clicks next/previous
	 * 
	 * @param cursor
	 */
	public void requestData(String cursor);

	/**
	 * called when the user clicks a heading (to re-sort results)
	 * 
	 * @param field
	 * @param direction
	 */
	public void resort(String field, String direction);

}
