package com.gallatinsystems.framework.gwt.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gallatinsystems.framework.gwt.util.client.FrameworkTextConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * this abstract widget provides basic support for rendering clickable lists of
 * items. It includes CSS definitions that will alter the style on mouse-over of
 * any of the options.
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class ListBasedWidget extends Composite implements
		ClickHandler, MouseOverHandler, MouseOutHandler {
	private static FrameworkTextConstants TEXT_CONSTANTS = GWT
			.create(FrameworkTextConstants.class);
	private static final String LOADING_CSS = "loading-label";
	private static final String LIST_ITEM_CSS = "clickable-list-item";
	private static final String LIST_ITEM_HOVER_CSS = "red-hover";
	private static final String EDIT_BUTTON_CSS = "edit-listitem-button";
	private static final String DEL_BUTTON_CSS = "delete-listitem-button";
	private static final String UNNAMED_TEXT = "unnamed";

	protected static enum ClickMode {
		OPEN, EDIT, DELETE, COPY, MOVE_UP, MOVE_DOWN, NEXT_PAGE, PREV_PAGE, INSERT
	};

	private Label loadingLabel;
	private VerticalPanel panel;
	private PageController controller;
	private List<String> cursorArray;
	private Button nextButton;
	private Button previousButton;
	private int currentPage;

	protected ListBasedWidget(PageController controller) {
		this.controller = controller;
		panel = new VerticalPanel();
		cursorArray = new ArrayList<String>();
		resetCursorArray();
		loadingLabel = new Label();
		loadingLabel.setText(TEXT_CONSTANTS.loading());
		loadingLabel.setStylePrimaryName(LOADING_CSS);
		nextButton = createButton(ClickMode.NEXT_PAGE, TEXT_CONSTANTS.next());
		previousButton = createButton(ClickMode.PREV_PAGE,
				TEXT_CONSTANTS.previous());
		panel.add(loadingLabel);
		initWidget(panel);
	}

	public Label createListEntry(String text) {
		Label l = new Label();
		l.setStylePrimaryName(LIST_ITEM_CSS);
		if (text != null && text.trim().length() > 0) {
			l.setText(text);
		} else {
			l.setText(UNNAMED_TEXT);
		}
		l.addMouseOutHandler(this);
		l.addMouseOverHandler(this);
		l.addClickHandler(this);
		return l;
	}

	public Button createButton(final ClickMode mode, String label) {
		Button button = new Button();
		if (label != null) {
			button.setText(label);
		}
		if (mode == ClickMode.EDIT) {
			button.setStylePrimaryName(EDIT_BUTTON_CSS);
		} else if (mode == ClickMode.EDIT) {
			button.setStylePrimaryName(DEL_BUTTON_CSS);
		} else if (mode == ClickMode.INSERT) {
			button.setStylePrimaryName(EDIT_BUTTON_CSS);
		}
		createClickableWidget(mode, button);
		return button;
	}

	public void createClickableWidget(final ClickMode mode,
			HasClickHandlers widget) {
		widget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleItemClick(event.getSource(), mode);
			}
		});
	}

	/**
	 * changes the currentPage and displays the "loading" message and changes
	 * the value of the current page pointer. It is up to subclasses to actually
	 * load the data.
	 * 
	 * TODO: evaluate this vs what I did in PaginatedDataTable.
	 * 
	 * @param increment
	 */
	protected void loadDataPage(int increment) {
		currentPage += increment;
		loadingLabel.setText(TEXT_CONSTANTS.loading());
		loadingLabel.setVisible(true);
	}

	/**
	 * resets current page to 0 and flushes the stored cursors
	 */
	protected void resetCursorArray() {
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
	protected String getCursor(int page) {
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
	protected void setCursor(String cursor) {
		if (currentPage < cursorArray.size()) {
			cursorArray.set(currentPage, cursor);
		} else {
			cursorArray.add(cursor);
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		((Label) event.getSource()).removeStyleName(LIST_ITEM_HOVER_CSS);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		((Label) event.getSource()).addStyleName(LIST_ITEM_HOVER_CSS);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (!controller.isWorking()) {
			handleItemClick(event.getSource(), ClickMode.OPEN);
		}
	}

	protected void toggleLoading(boolean show) {
		loadingLabel.setVisible(show);
	}

	protected void addWidget(Widget w) {
		panel.add(w);
	}

	@SuppressWarnings("rawtypes")
	protected void openPage(Class clazz, Map<String, Object> bundle) {
		controller.openPage(clazz, bundle);
	}

	protected void setWorking(boolean isWorking) {
		controller.setWorking(isWorking);
	}

	protected boolean isWorking() {
		return controller.isWorking();
	}

	protected Button getNextButton() {
		return nextButton;
	}

	protected Button getPreviousButtion() {
		return previousButton;
	}

	protected int getCurrentPage() {
		return currentPage;
	}

	protected PageController getPageController() {
		return controller;
	}

	protected abstract void handleItemClick(Object source, ClickMode mode);
}
