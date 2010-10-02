package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.SetRootRule;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * Widget that can handle the display of a sorted, paginated table of data
 * 
 * @author Christopher Fagiani
 * 
 */
public class PaginatedDataTable<T extends BaseDto> extends Composite implements
		ClickHandler {

	private static final String DOWN_IMG = "images/downarrow.gif";
	private static final String UP_IMG = "images/uparrow.gif";
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String DEFAULT_SORT_DIR = "asc";

	private String currentSortDirection;
	private String currentSortField;
	private String defaultSortField;
	private int currentSelection;
	private List<String> cursorArray;
	private Grid instanceGrid;
	private int currentPage;
	private DataTableListener<T> listener;
	private DataTableBinder<T> binder;
	private List<T> currentDtoList;
	private Button nextButton;
	private Button previousButton;
	private Label statusLabel;
	private VerticalPanel contentPanel;

	public PaginatedDataTable(String defaultSortField, DataTableListener<T> l,
			DataTableBinder<T> b) {
		contentPanel = new VerticalPanel();
		instanceGrid = new Grid();
		currentSelection = -1;
		currentSortDirection = DEFAULT_SORT_DIR;
		currentSortField = defaultSortField;
		this.defaultSortField = defaultSortField;
		cursorArray = new ArrayList<String>();
		currentDtoList = new ArrayList<T>();
		listener = l;
		binder = b;
		resetCursorArray();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		nextButton = new Button("Next");
		nextButton.setVisible(false);
		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadDataPage(1);
			}
		});

		previousButton = new Button("Previous");
		previousButton.setVisible(false);
		previousButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadDataPage(-1);
			}
		});
		buttonPanel.add(previousButton);
		buttonPanel.add(nextButton);
		statusLabel = new Label();
		contentPanel.add(instanceGrid);
		contentPanel.add(buttonPanel);
		initWidget(contentPanel);
	}

	private void loadDataPage(int increment) {
		currentPage += increment;
		statusLabel.setText("Loading...");
		statusLabel.setVisible(true);
		listener.requestData(getCursor(currentPage));
	}

	private void resetCursorArray() {
		currentPage = 0;
		cursorArray.clear();
	}

	private String getCursor(int page) {
		if (page >= 0) {
			if (page < cursorArray.size()) {
				if (cursorArray.get(page) != null
						&& cursorArray.get(page).trim().length() == 0) {
					return null;
				} else {
					return cursorArray.get(page);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private void setCursor(String cursor) {
		if (currentPage < cursorArray.size()) {
			cursorArray.set(currentPage, cursor);
		} else {
			cursorArray.add(cursor);
		}
	}

	private void loadHeaderRow() {
		String[] headings = binder.getHeaders();
		for (int i = 0; i < headings.length; i++) {
			addHeaderItem(i, headings[i]);
		}
		setGridRowStyle(instanceGrid, 0, false);
	}

	private void addHeaderItem(int col, final String text) {
		HorizontalPanel panel = new HorizontalPanel();
		Label temp = new Label(text);
		panel.add(temp);
		instanceGrid.setWidget(0, col, panel);
	}

	public void bindData(ArrayList<T> dtoList, String cursor, boolean isNew) {
		currentDtoList = dtoList;
		setCursor(cursor);
		statusLabel.setVisible(false);
		instanceGrid.clear();
		if (dtoList != null && dtoList.size() > 0) {
			instanceGrid.resize(dtoList.size() + 1, binder.getHeaders().length);
			loadHeaderRow();
			for (int i = 0; i < dtoList.size(); i++) {
				binder.bindRow(instanceGrid, dtoList.get(i), i + 1);
				setGridRowStyle(instanceGrid, i + 1, false);
			}
		} else {
			statusLabel.setText("No matches");
			statusLabel.setVisible(true);
		}

	}

	/**
	 * sets the css for a row in a grid. the top row will get the header style
	 * and other rows get either the even or odd style.
	 * 
	 * @param grid
	 * @param row
	 * @param selected
	 */
	private void setGridRowStyle(Grid grid, int row, boolean selected) {
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

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() instanceof Grid) {
			Grid grid = (Grid) event.getSource();

			// if we already had a selection, de-select it
			if (currentSelection > 0) {
				setGridRowStyle(grid, currentSelection, false);
			}
			Cell clickedCell = grid.getCellForEvent(event);
			// the click may not have been in a cell
			if (clickedCell != null) {
				int newSelection = clickedCell.getRowIndex();
				if (currentSelection != newSelection) {
					currentSelection = newSelection;

					// if the clicked cell is the header (row 0), don't change
					// the style
					if (currentSelection > 0
							&& currentSelection <= currentDtoList.size()) {
						setGridRowStyle(grid, currentSelection, true);
						listener.onItemSelected(currentDtoList
								.get(currentSelection - 1));
					} else {
						currentSelection = -1;
					}
				} else {
					currentSelection = -1;
				}
			}
		}
	}

	public String getCurrentSortDirection() {
		return currentSortDirection;
	}

	public String getCurrentSortField() {
		return currentSortField;
	}

}
