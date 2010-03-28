package com.gallatinsystems.framework.gwt.portlet.client;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Portlet extends FocusPanel implements HasDragHandle,
		ClickHandler {

	private static final String CLOSE_IMAGE = "images/close.png";
	private static final String CONF_IMAGE = "images/configure-32.png";
	private static final int BORDER_THICKNESS = 5;
	private static final String CSS_PANEL = "portlet-panel";
	private static final String CSS_HEADER = "portlet-header";
	private static final int HEADER_HEIGHT = 20;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;

	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private Widget currentColumn;

	private PortalContainer portletContainer;

	private int contentHeight;
	private int contentWidth;
	private boolean scrollable;
	private boolean configurable;
	private Widget internalContent;
	private Widget headerWidget;
	private Image closeImg;
	private Image confImg;
	private FocusPanel headerContainer;

	private boolean initialLoad = false;

	public Portlet(String title, boolean scrollable, boolean configurable,
			int width, int height) {
		addStyleName(CSS_PANEL);
		if (width > 0) {
			this.width = width;
		}
		if (height > 0) {
			this.height = height;
		}
		this.configurable = configurable;
		this.scrollable = scrollable;
		constructHeader(title);
	}

	protected void constructHeader(String title) {
		DockPanel headerPanel = new DockPanel();
		headerPanel.setWidth(width + "");
		headerPanel.setHeight(HEADER_HEIGHT + "");
		if (title != null) {
			headerWidget = new Label(title);
		} else {
			headerWidget = new Label("");
		}
		headerPanel.add(headerWidget, DockPanel.WEST);
		headerPanel.add(new SimplePanel(), DockPanel.CENTER);
		closeImg = new Image(CLOSE_IMAGE);
		closeImg.addClickHandler(this);
		headerPanel.add(closeImg, DockPanel.EAST);
		if (configurable) {
			confImg = new Image(CONF_IMAGE);
			confImg.addClickHandler(this);
			headerPanel.add(confImg, DockPanel.EAST);
		}

		headerWidget.setHeight(HEADER_HEIGHT + "");

		setPixelSize(width, getPortletHeight());
		headerContainer = new FocusPanel();
		headerContainer.addStyleName(CSS_HEADER);
		headerContainer.add(headerPanel);	
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == closeImg) {
			if (getReadyForRemove()) {
				portletContainer.removePortlet(this);
			}
		} else {
			handleConfigClick();
		}
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
		// update the width only if the header has already been rendered to the
		// DOM we need to do this to handle the portlets that use an async
		// callback to fetch their content (like the visualization api portlets)
		if (headerWidget.getOffsetWidth() > 0) {
			headerWidget.setPixelSize(headerWidget.getOffsetWidth(),
					HEADER_HEIGHT);
		}
	}

	public int getPortletHeight() {
		return height + HEADER_HEIGHT + BORDER_THICKNESS * 2;
	}

	public int getContentWidth() {
		return contentWidth;
	}

	public void setContentSize(int width, int height) {
		if (width != contentWidth) {
			contentWidth = width;
			headerContainer.setPixelSize(contentWidth, HEADER_HEIGHT);
		}
		if (height != contentHeight) {
			contentHeight = height;
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
					HEADER_HEIGHT);
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

	public boolean isConfigurable() {
		return configurable;
	}

	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}

	/**
	 * method that is invoked by the portlet container whenever another portlet
	 * raises an event.
	 * 
	 * @param e
	 */
	public abstract void handleEvent(PortletEvent e);

	/**
	 * called immediately before a portlet is removed from the ui. If you need
	 * to do any cleanup (persisting of data, for instance) before the portlet
	 * is closed, do it here. If the method returns false, then the close will
	 * be aborted.
	 */
	protected abstract boolean getReadyForRemove();

	/**
	 * handles the response to the click of the "configure" button
	 */
	protected abstract void handleConfigClick();
}
