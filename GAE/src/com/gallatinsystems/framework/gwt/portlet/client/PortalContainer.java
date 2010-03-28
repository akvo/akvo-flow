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

public abstract class PortalContainer extends SimplePanel {

	private static final int INITIAL_COL_HEIGHT = 750;
	private static final int MINIMUM_COL_WIDTH = 200;
	private static final int DEFAULT_COLS = 3;
	private static final int PADDING = 0;

	private List<Portlet> activePortlets;

	private static final String ACTIVE_COL_CSS = "portal-column-active";;
	private static final String IDLE_COL_CSS = "portal-column-idle";

	private PickupDragController dragController;
	private PortletDropController controller;
	private VerticalPanel[] columnPanels;
	private int columns = DEFAULT_COLS;
	private HorizontalPanel mainPanel;

	private boolean initialLoaded = false;

	/**
	 * Constructor for examples which create their own drag controller.
	 */
	public PortalContainer(int columnCount) {
		activePortlets = new ArrayList<Portlet>();
		final AbsolutePanel boundaryPanel = new AbsolutePanel();

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

			@Override
			public void onDragStart(DragStartEvent event) {
				for (int i = 0; i < columnPanels.length; i++) {
					columnPanels[i].setStyleName(ACTIVE_COL_CSS);
				}
			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event)
					throws VetoDragException {

			}

			@Override
			public void onPreviewDragStart(DragStartEvent event)
					throws VetoDragException {
				// TODO Auto-generated method stub

			}
		};

		// create a DragController to manage drag-n-drop actions
		// note: This creates an implicit DropController for the boundary panel
		dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorMultipleSelection(false);
		dragController.addDragHandler(handler);

		// initialize horizontal panel to hold our columns
		mainPanel = new HorizontalPanel();
		mainPanel.setSpacing(PADDING);
		boundaryPanel.add(mainPanel);

		for (int col = 0; col < columns; col++) {
			// initialize inner vertical panel to hold individual widgets
			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.setHeight(INITIAL_COL_HEIGHT + "");
			verticalPanel.setWidth(MINIMUM_COL_WIDTH + "");
			verticalPanel.setSpacing(PADDING);
			verticalPanel.setStyleName(IDLE_COL_CSS);
			columnPanels[col] = verticalPanel;

			// initialize a widget drop controller for the current column
			PortletDropController dropController = new PortletDropController(
					verticalPanel);
			dragController.registerDropController(dropController);

			// put a blank panel in each column
			SimplePanel p = new SimplePanel();
			p.setHeight(PADDING + "");
			verticalPanel.add(p);
			mainPanel.add(verticalPanel);
		}

	}

	protected PortletDropController getPortletController() {
		return controller;
	}

	protected void addDraggable(Widget w, int col) {
		columnPanels[col].add(w);
		if (w instanceof Portlet) {
			Portlet p = (Portlet) w;
			p.setParent(this);
			activePortlets.add(p);
			p.setCurrentColumn(columnPanels[col]);
		}
		dragController.makeDraggable(w);
	}

	public String getHistoryToken() {
		// TODO: fix this
		return "hi";
		// return getInvolvedClasses()[0].getSimpleName();
	}

	/**
	 * Return the classes involved in this example.
	 * 
	 * @return an array of involved classes
	 */
	public abstract Class<?>[] getInvolvedClasses();

	/**
	 * Called when {@link #onLoad()} is called for the first time.
	 */
	protected void onInitialLoad() {
	}

	/**
	 * Calls {@link #onInitialLoad()} when called for the first time.
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		if (!initialLoaded) {
			onInitialLoad();
			initialLoaded = true;
		}
	}

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
}
