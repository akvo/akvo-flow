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

	/**
	 * Constructs a new instance and sets the "loading" label up as the primary
	 * view.
	 * 
	 * @param controller
	 */
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

	/**
	 * creates an entry in the main data list using the display text passed in.
	 * the list item will be clickable and respond to mouse over events.
	 * 
	 * @param text
	 * @return
	 */
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

	/**
	 * creates a button attached to a row within the list. The button has a
	 * clickMode that defines its operation and a label used as the button text.
	 * 
	 * @param mode
	 *            - click mode (defines operation)
	 * @param label
	 *            - button text
	 * @return - new Button
	 */
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

	/**
	 * adds a click handler to a widget
	 * 
	 * @param mode
	 * @param widget
	 */
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

	/**
	 * removes hover style
	 * 
	 * @param event
	 */
	@Override
	public void onMouseOut(MouseOutEvent event) {
		((Label) event.getSource()).removeStyleName(LIST_ITEM_HOVER_CSS);
	}

	/**
	 * sets hover style
	 * 
	 * @param event
	 */
	@Override
	public void onMouseOver(MouseOverEvent event) {
		((Label) event.getSource()).addStyleName(LIST_ITEM_HOVER_CSS);
	}

	/**
	 * checks whether we're currently loading a new page and, if not, responds
	 * to a click event by delegating to the handleItemClick method.
	 * 
	 * @param event
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (!controller.isWorking()) {
			handleItemClick(event.getSource(), ClickMode.OPEN);
		}
	}

	/**
	 * hides or shows the Loading indicator
	 * 
	 * @param show
	 */
	protected void toggleLoading(boolean show) {
		loadingLabel.setVisible(show);
	}

	/**
	 * adds a widget to the base panel.
	 * 
	 * @param w
	 */
	protected void addWidget(Widget w) {
		panel.add(w);
	}

	/**
	 * sends an openPage call to the controller bound to this widget (thus
	 * allowing you to open a full-page widget).
	 * 
	 * @param clazz
	 * @param bundle
	 */
	@SuppressWarnings("rawtypes")
	protected void openPage(Class clazz, Map<String, Object> bundle) {
		controller.openPage(clazz, bundle);
	}

	/**
	 * notifies the controller that the widget is busy processing something so
	 * it can suppress other click events.
	 * 
	 * @param isWorking
	 */
	protected void setWorking(boolean isWorking) {
		controller.setWorking(isWorking);
	}

	/**
	 * returns true if the controller is working, false if not.
	 * @return
	 */
	protected boolean isWorking() {
		return controller.isWorking();
	}

	/**
	 * gets the "next" button in the case where pagination is needed
	 * 
	 * @return
	 */
	protected Button getNextButton() {
		return nextButton;
	}

	/**
	 * gets the "previous" button in the case where pagination is needed
	 * 
	 * @return
	 */
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
