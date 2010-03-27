package com.gallatinsystems.framework.gwt.portlet.client;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PortalContainer extends SimplePanel {

	private static final int COLS = 3;
	private static final int PADDING = 0;

	private List<Portlet> activePortlets;

	private static final String COL_CSS = "portal-column";

	private PickupDragController dragController;
	private PortletDropController controller;
	private VerticalPanel[] columnPanels;

	private boolean initialLoaded = false;

	/**
	 * Constructor for examples which create their own drag controller.
	 */
	public PortalContainer() {
		activePortlets = new ArrayList<Portlet>();
		final AbsolutePanel boundaryPanel = new AbsolutePanel();
		boundaryPanel.setPixelSize(1024, 768);
		setWidget(boundaryPanel);
		columnPanels = new VerticalPanel[COLS];

		// right now, we don't do anything special for the drag handler
		DragHandler handler = new DragHandlerAdapter();

		// create a DragController to manage drag-n-drop actions
		// note: This creates an implicit DropController for the boundary panel
		dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorMultipleSelection(false);
		dragController.addDragHandler(handler);

		// initialize horizontal panel to hold our columns
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(PADDING);
		boundaryPanel.add(horizontalPanel);

		for (int col = 0; col < COLS; col++) {

			// initialize inner vertical panel to hold individual widgets
			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.setSpacing(PADDING);
			verticalPanel.setStyleName(COL_CSS);
			columnPanels[col] = verticalPanel;

			// initialize a widget drop controller for the current column
			PortletDropController dropController = new PortletDropController(
					verticalPanel);
			dragController.registerDropController(dropController);

			// put a blank panel in each column
			SimplePanel p = new SimplePanel();
			p.setHeight(PADDING + "");
			verticalPanel.add(p);

			// Put together the column pieces
			// Label heading = new Label("Column " + col);
			// heading.addStyleName(CSS_DEMO_INSERT_PANEL_EXAMPLE_HEADING);
			// columnCompositePanel.add(heading);
			horizontalPanel.add(verticalPanel);
		}

	}

	protected PortletDropController getPortletController() {
		return controller;
	}

	protected void addDraggable(Widget w, int col) {
		// RootPanel.get().add(w);
		columnPanels[col].add(w);
		if (w instanceof Portlet) {
			((Portlet) w).setParent(this);
			activePortlets.add((Portlet) w);
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
}
