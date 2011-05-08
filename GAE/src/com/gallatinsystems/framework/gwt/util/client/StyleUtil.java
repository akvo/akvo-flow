package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.user.client.ui.Grid;

/**
 * utility class for ui styling
 * 
 * @author Christopher Fagiani
 * 
 */
public class StyleUtil {
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";

	/**
	 * sets the css for a row in a grid. the top row will get the header style
	 * and other rows get either the even or odd style.
	 * 
	 * @param grid
	 * @param row
	 * @param selected
	 */
	public static void setGridRowStyle(Grid grid, int row, boolean selected) {
		String style = "";
		if (row > 0) {
			if (selected) {
				style = SELECTED_ROW_CSS;
			} else {
				if (row % 2 == 0) {
					style = EVEN_ROW_CSS;
				} else {
					style = ODD_ROW_CSS;
				}
			}
		} else {
			style = GRID_HEADER_CSS;
		}
		for (int i = 0; i < grid.getColumnCount(); i++) {
			grid.getCellFormatter().setStyleName(row, i, style);
		}
	}
}
