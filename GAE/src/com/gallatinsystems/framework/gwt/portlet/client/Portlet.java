package com.gallatinsystems.framework.gwt.portlet.client;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
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
	

	private static final int BORDER_THICKNESS = 5;	

	private static final String CSS_PANEL = "portlet-panel";

	private static final String CSS_HEADER = "portlet-header";

	private PortalContainer portletContainer;


	private int contentHeight;
	private boolean scrollable;
	private Widget internalContent;
	private int contentWidth;	
	private final FocusPanel headerContainer;
	private final Widget headerWidget;
	private boolean initialLoad = false;
	
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
		add(verticalPanel);

		
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
			headerContainer.setPixelSize(contentWidth, HEADER_HEIGHT);
//			northWidget.setPixelSize(contentWidth, BORDER_THICKNESS);
	//		southWidget.setPixelSize(contentWidth, BORDER_THICKNESS);
		}
		if (height != contentHeight) {
			contentHeight = height;			
		//	westWidget.setPixelSize(BORDER_THICKNESS, contentHeight
					//+ HEADER_HEIGHT);
			//eastWidget.setPixelSize(BORDER_THICKNESS, contentHeight
				//	+ HEADER_HEIGHT);
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
