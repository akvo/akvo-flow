package com.gallatinsystems.framework.gwt.portlet.client;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Portlet extends FocusPanel implements HasDragHandle {

	private static final int HEADER_HEIGHT = 20;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private Widget currentColumn;
	/**
	 * Specifies that resizing occur at the east edge.
	 */
	public static final int DIRECTION_EAST = 0x0001;

	/**
	 * Specifies that resizing occur at the both edge.
	 */
	public static final int DIRECTION_NORTH = 0x0002;

	/**
	 * Specifies that resizing occur at the south edge.
	 */
	public static final int DIRECTION_SOUTH = 0x0004;

	/**
	 * Specifies that resizing occur at the west edge.
	 */
	public static final int DIRECTION_WEST = 0x0008;

	/**
	 * Specifies that resizing occur at the east edge.
	 */
	public static final DirectionConstant EAST = new DirectionConstant(
			DIRECTION_EAST, "e");

	/**
	 * Specifies that resizing occur at the both edge.
	 */
	public static final DirectionConstant NORTH = new DirectionConstant(
			DIRECTION_NORTH, "n");

	/**
	 * Specifies that resizing occur at the north-east edge.
	 */
	public static final DirectionConstant NORTH_EAST = new DirectionConstant(
			DIRECTION_NORTH | DIRECTION_EAST, "ne");

	/**
	 * Specifies that resizing occur at the north-west edge.
	 */
	public static final DirectionConstant NORTH_WEST = new DirectionConstant(
			DIRECTION_NORTH | DIRECTION_WEST, "nw");

	/**
	 * Specifies that resizing occur at the south edge.
	 */
	public static final DirectionConstant SOUTH = new DirectionConstant(
			DIRECTION_SOUTH, "s");

	/**
	 * Specifies that resizing occur at the south-east edge.
	 */
	public static final DirectionConstant SOUTH_EAST = new DirectionConstant(
			DIRECTION_SOUTH | DIRECTION_EAST, "se");

	/**
	 * Specifies that resizing occur at the south-west edge.
	 */
	public static final DirectionConstant SOUTH_WEST = new DirectionConstant(
			DIRECTION_SOUTH | DIRECTION_WEST, "sw");

	/**
	 * Specifies that resizing occur at the west edge.
	 */
	public static final DirectionConstant WEST = new DirectionConstant(
			DIRECTION_WEST, "w");

	private static final int BORDER_THICKNESS = 5;

	private static final String CSS_BORDER_EDGE = "portlet-edge";

	private static final String CSS_PANEL = "portlet-panel";

	private static final String CSS_HEADER = "portlet-header";

	private PortalContainer portletContainer;

	public static class DirectionConstant {

		public final int directionBits;

		public final String directionLetters;

		private DirectionConstant(int directionBits, String directionLetters) {
			this.directionBits = directionBits;
			this.directionLetters = directionLetters;
		}
	}

	private int contentHeight;
	private boolean scrollable;
	private Widget internalContent;
	private int contentWidth;
	private Widget eastWidget;
	private Grid grid = new Grid(3, 3);
	private final FocusPanel headerContainer;
	private final Widget headerWidget;
	private boolean initialLoad = false;
	private Widget northWidget;
	private Widget southWidget;
	private Widget westWidget;

	public Portlet(String title, boolean scrollable, int width, int height) {
		addStyleName(CSS_PANEL);
		if (width > 0) {
			this.width = width;
		}
		if (height > 0) {
			this.height = height;
		}

		this.scrollable = scrollable;
		if (title != null) {
			headerWidget = new Label(title);
		} else {
			headerWidget = new Label("");
		}
		headerWidget.setHeight(HEADER_HEIGHT + "");

		setPixelSize(width, getPortletHeight());
		headerContainer = new FocusPanel();
		headerContainer.addStyleName(CSS_HEADER);
		headerContainer.add(headerWidget);

	}

	protected void setContent(Widget contentWidget) {
		if (contentWidget != null) {
			internalContent = scrollable ? new ScrollPanel(contentWidget)
					: contentWidget;
		}
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(headerContainer);
		verticalPanel.add(internalContent);

		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		add(grid);

		setupCell(0, 0, NORTH_WEST);
		northWidget = setupCell(0, 1, NORTH);
		setupCell(0, 2, NORTH_EAST);

		westWidget = setupCell(1, 0, WEST);
		grid.setWidget(1, 1, verticalPanel);
		eastWidget = setupCell(1, 2, EAST);

		setupCell(2, 0, SOUTH_WEST);
		southWidget = setupCell(2, 1, SOUTH);
		setupCell(2, 2, SOUTH_EAST);
		setContentSize(width, height);
	}

	public int getPortletHeight() {
		return height + HEADER_HEIGHT + BORDER_THICKNESS * 2;
	}

	public int getContentWidth() {
		return contentWidth;
	}

	public void moveBy(int right, int down) {
		AbsolutePanel parent = (AbsolutePanel) getParent();
		Location location = new WidgetLocation(this, parent);
		int left = location.getLeft() + right;
		int top = location.getTop() + down;
		parent.setWidgetPosition(this, left, top);
	}

	public void setContentSize(int width, int height) {
		if (width != contentWidth) {
			contentWidth = width;
			headerContainer.setPixelSize(contentWidth, headerWidget
					.getOffsetHeight());
			northWidget.setPixelSize(contentWidth, BORDER_THICKNESS);
			southWidget.setPixelSize(contentWidth, BORDER_THICKNESS);
		}
		if (height != contentHeight) {
			contentHeight = height;
			int headerHeight = headerContainer.getOffsetHeight();
			westWidget.setPixelSize(BORDER_THICKNESS, contentHeight
					+ headerHeight);
			eastWidget.setPixelSize(BORDER_THICKNESS, contentHeight
					+ headerHeight);
		}
		internalContent.setPixelSize(contentWidth, contentHeight);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (!initialLoad && internalContent != null
				&& internalContent.getOffsetHeight() != 0) {
			initialLoad = true;
			headerWidget.setPixelSize(headerWidget.getOffsetWidth(),
					headerWidget.getOffsetHeight());
			setContentSize(internalContent.getOffsetWidth(), internalContent
					.getOffsetHeight());
		}
	}

	private Widget setupCell(int row, int col, DirectionConstant direction) {
		final FocusPanel widget = new FocusPanel();
		widget.setPixelSize(BORDER_THICKNESS, BORDER_THICKNESS);
		grid.setWidget(row, col, widget);
		grid.getCellFormatter().addStyleName(row, col, CSS_BORDER_EDGE);
		return widget;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	void setParent(PortalContainer container) {
		this.portletContainer = container;
	}

	/**
	 * Tells the portlet container to notify all other portlets that something
	 * happened in this portlet that they MAY want to know about. Events raised
	 * by a portlet are delivered to all portlets EXCEPT the one that raised it
	 * 
	 * @param e
	 */
	protected void raiseEvent(PortletEvent e) {
		if (portletContainer != null) {
			portletContainer.notifyPortlets(e);
		}
	}

	/**
	 * returns the portion of the Portlet that can be used as a drag handle. By
	 * default, this will return the header. This can be overriden by a subclass
	 * that wants to use some other drag handler.
	 */
	public Widget getDragHandle() {
		return headerContainer;
	}

	public Widget getCurrentColumn() {
		return currentColumn;
	}

	public void setCurrentColumn(Widget currentColumn) {
		this.currentColumn = currentColumn;
	}

	/**
	 * method that is invoked by the portlet container whenever another portlet
	 * raises an event.
	 * 
	 * @param e
	 */
	public abstract void handleEvent(PortletEvent e);

}
