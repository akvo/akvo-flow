package com.gallatinsystems.framework.gwt.component;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.util.client.FrameworkTextConstants;
import com.gallatinsystems.framework.gwt.util.client.StyleUtil;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * Widget that can handle the display of a sorted, paginated table of data. This
 * class can handle arrayLists of objects that descend from BaseDto
 * 
 * @author Christopher Fagiani
 * 
 */
public class PaginatedDataTable<T extends BaseDto> extends Composite implements
		ClickHandler {
	private static FrameworkTextConstants TEXT_CONSTANTS = GWT
			.create(FrameworkTextConstants.class);

	public static final String ASC_SORT = "asc";
	public static final String DSC_SORT = "desc";
	private static final String HEADER_CSS = "datatable-header";
	private static final String DOWN_IMG = "images/downarrow.gif";
	private static final String UP_IMG = "images/uparrow.gif";

	private static final String DEFAULT_SORT_DIR = ASC_SORT;

	private String currentSortDirection;
	private String currentSortField;
	private String defaultSortField;
	private int currentSelection;

	public int getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(int currentSelection) {
		this.currentSelection = currentSelection;
	}

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
	private boolean rowClickable;
	private boolean sortOverriden;

	public Grid getGrid() {
		return instanceGrid;
	}

	/**
	 * constructs a data table with rows that will be populated by a
	 * DataTableBinder. The DataTableListener will be notified when the user
	 * selects a specific item. This constructor expects that the client code
	 * will be populating the table immediately and thus will display the
	 * "loading" label until bindData is called.
	 * 
	 * @param defaultSortField
	 * @param l
	 * @param b
	 * @param isRowClickable
	 */
	public PaginatedDataTable(String defaultSortField, DataTableListener<T> l,
			DataTableBinder<T> b, boolean isRowClickable) {
		this(defaultSortField, l, b, isRowClickable, true);
	}

	/**
	 * constructs a data table with rows that will be populated by a
	 * DataTableBinder. The DataTableListener will be notified when the user
	 * selects a specific item.
	 * 
	 * @param defaultSortField
	 * @param l
	 * @param b
	 * @param isRowClickable
	 * @param displayLoading
	 */
	public PaginatedDataTable(String defaultSortField, DataTableListener<T> l,
			DataTableBinder<T> b, boolean isRowClickable, boolean displayLoading) {
		contentPanel = new VerticalPanel();
		instanceGrid = new Grid();
		this.rowClickable = isRowClickable;
		if (rowClickable) {
			instanceGrid.addClickHandler(this);
		}
		currentSelection = -1;
		currentSortDirection = DEFAULT_SORT_DIR;
		currentSortField = defaultSortField;
		this.defaultSortField = defaultSortField;
		cursorArray = new ArrayList<String>();
		currentDtoList = new ArrayList<T>();
		sortOverriden = false;
		listener = l;
		binder = b;
		resetCursorArray();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		nextButton = new Button(TEXT_CONSTANTS.next());
		nextButton.setVisible(false);
		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadDataPage(1);
			}
		});

		previousButton = new Button(TEXT_CONSTANTS.previous());
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
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(displayLoading);
		contentPanel.add(statusLabel);
		contentPanel.add(instanceGrid);
		contentPanel.add(buttonPanel);
		initWidget(contentPanel);
	}

	/**
	 * changes the currentPage and displays the "loading" message then invokes
	 * the "requestData" method on the listener interface.
	 * 
	 * @param increment
	 */
	private void loadDataPage(int increment) {
		currentPage += increment;
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(true);
		listener.requestData(getCursor(currentPage - 1), false);
	}

	/**
	 * resets current page to 0 and flushes the stored cursors
	 */
	private void resetCursorArray() {
		currentPage = 0;
		cursorArray.clear();
	}

	/**
	 * gets the cursor that corresponds to the page passed in (if it has been
	 * loaded).
	 * 
	 * @param page
	 * @return
	 */
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

	/**
	 * stores the cursor passed in for the currentPage
	 * 
	 * @param cursor
	 */
	private void setCursor(String cursor) {
		if (currentPage < cursorArray.size()) {
			cursorArray.set(currentPage, cursor);
		} else {
			cursorArray.add(cursor);
		}
	}

	/**
	 * gets the headers to use for the table from the data binder and then adds
	 * them to the grid
	 */
	private void loadHeaderRow() {
		DataTableHeader[] headings = binder.getHeaders();
		for (int i = 0; i < headings.length; i++) {
			addHeaderItem(i, headings[i]);
		}
		StyleUtil.setGridRowStyle(instanceGrid, 0, false);
	}

	/**
	 * installs the label widgets for the column headers
	 * 
	 * @param col
	 * @param text
	 */
	private void addHeaderItem(int col, final DataTableHeader header) {
		HorizontalPanel panel = new HorizontalPanel();
		Label temp = ViewUtil.initLabel(header.getDisplayName(), HEADER_CSS);
		panel.add(temp);
		instanceGrid.setWidget(0, col, panel);
		if (header.isSortable()) {

			ClickHandler handler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (currentSortField.equals(header.getFieldName())) {
						if (ASC_SORT.equals(currentSortDirection)) {
							currentSortDirection = DSC_SORT;
						} else {
							currentSortDirection = ASC_SORT;
						}
					} else {
						currentSortField = header.getFieldName();
						currentSortDirection = DEFAULT_SORT_DIR;
					}
					resort();
				}
			};
			temp.addClickHandler(handler);

			// render the appropriate image for the sort direction
			if (currentSortField.equals(header.getFieldName())) {
				if (currentSortDirection != null
						&& currentSortDirection.equals(ASC_SORT)) {
					Image img = new Image(UP_IMG);
					img.addClickHandler(handler);
					panel.add(img);
				} else {
					Image img = new Image(DOWN_IMG);
					img.addClickHandler(handler);
					panel.add(img);
				}
			}
		}
	}

	/**
	 * resets the cursor array and then calls the resort method on the listener.
	 * 
	 * @param field
	 * @param dir
	 */
	private void resort() {
		statusLabel.setText(TEXT_CONSTANTS.pleaseWait());
		statusLabel.setVisible(true);
		listener.requestData(null, true);
	}

	/**
	 * adds a new row to the bottom of this grid and calls bindData on the new
	 * row, passing null for the instance value. This method can be used to
	 * support in-table editors where the user can click "add" to create a new
	 * item in-place.
	 */
	public void addNewRow() {
		if (instanceGrid != null) {
			Integer rowCount = instanceGrid.getRowCount();
			if (rowCount > 0) {
				instanceGrid.insertRow(rowCount);
			} else {
				statusLabel.setVisible(false);
				instanceGrid.resize(2, binder.getHeaders().length);
				loadHeaderRow();
			}
			binder.bindRow(instanceGrid, null, instanceGrid.getRowCount() - 1);
		}
	}

	/**
	 * populates the data grid with the dtos passed in
	 * 
	 * @param dtoList
	 * @param cursor
	 * @param isNew
	 */
	public void bindData(ArrayList<T> dtoList, String cursor, boolean isNew,
			boolean isResort) {
		currentDtoList = dtoList;
		currentSelection = -1;
		if (isNew) {
			resetCursorArray();
			if (!isResort && !sortOverriden) {
				currentSortDirection = ASC_SORT;
				currentSortField = defaultSortField;
			}
		}
		setCursor(cursor);
		statusLabel.setVisible(false);
		instanceGrid.clear();
		if (dtoList != null && dtoList.size() > 0) {
			instanceGrid.resize(dtoList.size() + 1, binder.getHeaders().length);
			loadHeaderRow();
			for (int i = 0; i < dtoList.size(); i++) {
				binder.bindRow(instanceGrid, dtoList.get(i), i + 1);
				StyleUtil.setGridRowStyle(instanceGrid, i + 1, false);
			}

		} else {
			instanceGrid.resize(0, 0);
			statusLabel.setText(TEXT_CONSTANTS.noMatches());
			statusLabel.setVisible(true);
		}
		if (currentDtoList != null
				&& currentDtoList.size() >= binder.getPageSize()) {
			nextButton.setVisible(true);
		} else {
			nextButton.setVisible(false);
		}
		if (currentPage > 0) {
			previousButton.setVisible(true);
		} else {
			previousButton.setVisible(false);
		}
		sortOverriden = false;
	}

	/**
	 * handles the click of rows within the grid
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() instanceof Grid) {
			Grid grid = (Grid) event.getSource();

			// if we already had a selection, de-select it
			if (currentSelection > 0) {
				StyleUtil.setGridRowStyle(grid, currentSelection, false);
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
						StyleUtil.setGridRowStyle(grid, currentSelection, true);
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

	public void overrideSort(String field, String dir) {
		currentSortDirection = dir;
		currentSortField = field;
		sortOverriden = true;

	}

	public String getCurrentSortField() {
		return currentSortField;
	}

	/**
	 * adds the widget passed in to the bottom of the grid
	 * 
	 * @param w
	 */
	public void appendRow(Widget w) {
		int rowCount = instanceGrid.getRowCount();
		instanceGrid.resizeRows(rowCount + 1);
		instanceGrid.setWidget(rowCount, 0, w);

	}

	public void removeRow(Integer row) {
		instanceGrid.removeRow(row);
	}
}
