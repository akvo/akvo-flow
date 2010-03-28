package com.gallatinsystems.framework.gwt.portlet.client;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base portlet container class that implements a very simplistic portal-like
 * environment. The container will create an arbitrary number of columns that
 * can contain any number of portlets. Portlets added from the top of the column
 * to the bottom (you can arrange them in any order, but you can't have an empty
 * space above a portlet). This base class will also handle broadcasting portlet
 * events raised by any of the portlets it's managing to all others.
 * 
 * TODO: make it so non-draggable widgets are "locked" in their position and
 * can't get bumped by draggables
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class PortalContainer extends SimplePanel {

	private static final int INITIAL_COL_HEIGHT = 750;
	private static final int MINIMUM_COL_WIDTH = 200;
	private static final int DEFAULT_COLS = 3;
	private static final int PADDING = 0;

	private List<Portlet> activePortlets;

	private static final String ACTIVE_COL_CSS = "portal-column-active";;
	private static final String IDLE_COL_CSS = "portal-column-idle";

	private PickupDragController dragController;
	private VerticalPanel[] columnPanels;
	private int columns = DEFAULT_COLS;
	private HorizontalPanel mainPanel;

	/**
	 * Initializes the portal with columnCount columns.
	 * 
	 * @param columnCount
	 *            - positive integer representing the number of columns to
	 *            spawn. If the value passed in is zero or negative, the DEFAULT
	 *            column count will be used.
	 */
	public PortalContainer(int columnCount) {
		activePortlets = new ArrayList<Portlet>();
		final AbsolutePanel boundaryPanel = new AbsolutePanel();
		// since we're extending SimplePanel, install the AbsolutePanel as our
		// sole widget
		setWidget(boundaryPanel);
		if (columnCount > 0) {
			columns = columnCount;
		}
		columnPanels = new VerticalPanel[columns];

		// define custom events that occur when dragging
		DragHandler handler = new DragHandler() {

			/**
			 * update the currentColumn pointer in the portlet and make sure
			 * that we don't collapse empty columns down to nothing.
			 */
			@Override
			public void onDragEnd(DragEndEvent event) {
				if (event.getSource() instanceof Portlet) {
					Portlet p = (Portlet) event.getSource();
					Widget w = p.getCurrentColumn();
					if (w.getOffsetWidth() < MINIMUM_COL_WIDTH) {
						w.setWidth(MINIMUM_COL_WIDTH + "");
						w.setHeight(INITIAL_COL_HEIGHT + "");
					}
					if (event.getContext().finalDropController != null) {
						p
								.setCurrentColumn(event.getContext().finalDropController
										.getDropTarget());
					}
					for (int i = 0; i < columnPanels.length; i++) {
						columnPanels[i].setStyleName(IDLE_COL_CSS);
					}
				}
			}

			/**
			 * swaps out the CSS for the columns so they have visible borders
			 * while dragging
			 */
			@Override
			public void onDragStart(DragStartEvent event) {
				for (int i = 0; i < columnPanels.length; i++) {
					columnPanels[i].setStyleName(ACTIVE_COL_CSS);
				}
			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event)
					throws VetoDragException {
				// no-op
			}

			@Override
			public void onPreviewDragStart(DragStartEvent event)
					throws VetoDragException {
				// no-op
			}
		};

		dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorMultipleSelection(false);
		dragController.addDragHandler(handler);

		// the main panel is the container for all the columns
		mainPanel = new HorizontalPanel();
		mainPanel.setSpacing(PADDING);
		boundaryPanel.add(mainPanel);

		for (int col = 0; col < columns; col++) {
			// initialize inner vertical panel to hold individual portlets
			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.setHeight(INITIAL_COL_HEIGHT + "");
			verticalPanel.setWidth(MINIMUM_COL_WIDTH + "");
			verticalPanel.setSpacing(PADDING);
			verticalPanel.setStyleName(IDLE_COL_CSS);
			columnPanels[col] = verticalPanel;

			// initialize a widget drop controller for the current column and
			// register it with the drag controller
			PortletDropController dropController = new PortletDropController(
					verticalPanel);
			dragController.registerDropController(dropController);

			mainPanel.add(verticalPanel);
		}
	}

	/**
	 * adds a portlet to the list managed by this portal in the column passed
	 * in. The portlet will be added beneath the other portlets already in that
	 * column. If the draggableFlag is true, tt will also be registered with the
	 * drag controller so it can be moved by the user
	 * 
	 * 
	 * @param w
	 *            - widget to add
	 * @param col
	 *            - column in which widget should be added (zero-indexed)
	 * @param draggable
	 *            - flag indicating whether or not the widget is draggable
	 */
	protected void addPortlet(Widget w, int col, boolean draggable) {
		columnPanels[col].add(w);
		if (w instanceof Portlet) {
			Portlet p = (Portlet) w;
			p.setParent(this);
			activePortlets.add(p);
			p.setCurrentColumn(columnPanels[col]);
		}
		if (draggable) {
			dragController.makeDraggable(w);
		}
	}

	/**
	 * returns a token to be used for history to detect if users click the back
	 * button
	 * 
	 * @return
	 */
	public String getHistoryToken() {
		return getInvolvedClasses()[0].getName();
	}

	/**
	 * propagates the PortletEvent to all active portlets EXECPT the one that
	 * raised it
	 * 
	 * @param e
	 */
	public void notifyPortlets(PortletEvent e) {
		for (Portlet p : activePortlets) {
			if (e.getSource() != p) {
				p.handleEvent(e);
			}
		}
	}

	/**
	 * removes a portlet from the UI
	 * 
	 * @param p
	 */
	public void removePortlet(Portlet p) {
		p.removeFromParent();
		activePortlets.remove(p);
	}

	/**
	 * Return the classes involved
	 * 
	 * @return an array of involved classes
	 */
	public abstract Class<?>[] getInvolvedClasses();

}
