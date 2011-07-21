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

/**
 * Base class for a portlet that can be used in conjunction with a
 * PortalContainer. Portlets are designed to be dragged and dropped within the
 * portalContainer and may support user configuration. All portlets have a
 * header with a name that is used as the dragHandle (i.e. you must drag a
 * portlet using its title bar).
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class Portlet extends FocusPanel implements HasDragHandle {
	/**
	 * both name and description should be hidden by subclasses
	 */
	public static final String NAME = "Generic Portlet";
	public static final String DESCRIPTION = "Generic Portlet";

	private static final String CLOSE_IMAGE = "images/close.png";
	private static final String CONF_IMAGE = "images/configure-32.png";
	private static final String SNAP_IMAGE = "images/camera_icon.png";
	private static final String CSS_PANEL = "portlet-panel";
	private static final String CSS_HEADER = "portlet-header";
	private static final String PORTLET_TITLE_CSS = "portlet-title";
	private static final int HEADER_HEIGHT = 20;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;
	protected static final int FULLSCREEN_WIDTH = 1600;
	protected static final int FULLSCREEN_HEIGHT = 1200;
	private static final String CONF_TOOLTIP = "Save current portlet configuration";
	private static final String CLOSE_TOOLTIP = "Remove portlet";
	private static final String SNAP_TOOLTIP = "Export Snapshot Image";

	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private Widget currentColumn;

	private PortalContainer portletContainer;

	private boolean active;
	private int contentHeight;
	private int contentWidth;
	private boolean scrollable;
	private boolean configurable;
	private boolean snapable;
	private Widget internalContent;
	private Widget headerWidget;
	private Image closeImg;
	private Image confImg;
	private Image snapshotImg;
	private FocusPanel headerContainer;
	private String config;

	private boolean isLoaded = false;

	/**
	 * constructs a portlet that cannot take snapshots
	 * 
	 * @param title
	 * @param scrollable
	 * @param configurable
	 * @param width
	 * @param height
	 */
	public Portlet(String title, boolean scrollable, boolean configurable,
			int width, int height) {
		this(title, scrollable, configurable, false, width, height);
	}

	/**
	 * constructs a portlet with the given title. If scrollable is true, the
	 * content panel will be wrapped in a scrollPanel. If configurable is true,
	 * the header will include the "configure" button.
	 * 
	 * @param title
	 * @param scrollable
	 * @param configurable
	 *            - whether or not to display the configuration icon
	 * @param snapable
	 *            - whether or not to display the snapshot icon
	 * @param width
	 *            - desired max width of the widget content panel
	 * @param height
	 *            - desired max height of the widget's content panel (i.e. does
	 *            not include the header)
	 */
	public Portlet(String title, boolean scrollable, boolean configurable,
			boolean snapable, int width, int height) {
		addStyleName(CSS_PANEL);
		if (width > 0) {
			this.width = width;
		}
		if (height > 0) {
			this.height = height;
		}
		this.configurable = configurable;
		this.scrollable = scrollable;
		this.snapable = snapable;
		active = true;
		constructHeader(title);
	}

	/**
	 * constructs the standard portlet header. This consists of a left-justified
	 * title, and a right-justified close button. If this portlet is
	 * configurable, a configure button will be added to the left of the close
	 * button.
	 * 
	 * @param title
	 */
	protected void constructHeader(String title) {
		DockPanel headerPanel = new DockPanel();
		headerPanel.setWidth(width + "");
		headerPanel.setHeight(HEADER_HEIGHT + "");
		if (title != null) {
			headerWidget = new Label(title);
		} else {
			headerWidget = new Label("");
		}
		headerWidget.setStyleName(PORTLET_TITLE_CSS);
		headerPanel.add(headerWidget, DockPanel.WEST);
		headerPanel.add(new SimplePanel(), DockPanel.CENTER);
		closeImg = new Image(CLOSE_IMAGE);
		closeImg.addClickHandler(new ClickHandler() {
			/**
			 * If the close button is clicked, getReadyForRemove is called
			 * (allowing sub-classes to do any cleanup they want) then the
			 * portal container is notified that is should remove this portlet.
			 */
			@Override
			public void onClick(ClickEvent event) {
				if (getReadyForRemove()) {
					portletContainer.removePortlet(Portlet.this);
				}
			}
		});
		closeImg.setTitle(CLOSE_TOOLTIP);
		headerPanel.add(closeImg, DockPanel.EAST);
		if (configurable) {
			confImg = new Image(CONF_IMAGE);
			confImg.setTitle(CONF_TOOLTIP);
			confImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					handleConfigClick();

				}
			});
			headerPanel.add(confImg, DockPanel.EAST);
		}
		if (snapable) {
			snapshotImg = new Image(SNAP_IMAGE);
			snapshotImg.setTitle(SNAP_TOOLTIP);
			snapshotImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					handleExportClick();

				}
			});
			headerPanel.add(snapshotImg, DockPanel.EAST);

		}

		headerWidget.setHeight(HEADER_HEIGHT + "");

		setPixelSize(width, getPortletHeight());
		headerContainer = new FocusPanel();
		headerContainer.addStyleName(CSS_HEADER);
		headerContainer.add(headerPanel);
	}

	/**
	 * sets the internal content of the widget and updates the size of the
	 * header (if needed) to match
	 * 
	 * @param contentWidget
	 */
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

	/**
	 * returns the total height of the portlet
	 * 
	 * @return
	 */
	public int getPortletHeight() {
		return height + HEADER_HEIGHT;
	}

	/**
	 * returns the total width of the portlet's content panel
	 * 
	 * @return
	 */
	public int getContentWidth() {
		return contentWidth;
	}

	/**
	 * sets the size of the portlet's content pane
	 * 
	 * @param width
	 * @param height
	 */
	protected void setContentSize(int width, int height) {
		if (width != contentWidth) {
			contentWidth = width;
			headerContainer.setPixelSize(contentWidth, HEADER_HEIGHT);
		}
		if (height != contentHeight) {
			contentHeight = height;
		}
		internalContent.setPixelSize(contentWidth, contentHeight);
	}

	/**
	 * sets the size of the header and content if they've already been added to
	 * the DOM
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		if (!isLoaded && internalContent != null
				&& internalContent.getOffsetHeight() != 0) {
			isLoaded = true;
			headerWidget.setPixelSize(headerWidget.getOffsetWidth(),
					HEADER_HEIGHT);
			setContentSize(internalContent.getOffsetWidth(),
					internalContent.getOffsetHeight());
		}
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
		return headerWidget;
	}

	/**
	 * returns the column in which this portlet is currently located
	 * 
	 * @return
	 */
	public Widget getCurrentColumn() {
		return currentColumn;
	}

	/**
	 * updates the column
	 * 
	 * @param currentColumn
	 */
	public void setCurrentColumn(Widget currentColumn) {
		this.currentColumn = currentColumn;
	}

	public boolean isConfigurable() {
		return configurable;
	}

	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
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

	public boolean isActive() {
		return active;
	}

	public void disable() {
		active = false;
	}

	public void enable() {
		active = true;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setShowFullscreen(boolean showFullscreen) {
		if (showFullscreen) {
			closeImg.setVisible(false);
			setPixelSize(getFullscreenWidth(), getFullscreenHeight());

		}
	}

	/**
	 * method that is invoked by the portlet container whenever another portlet
	 * raises an event. Subclasses should override this method if they want to
	 * react to portlet events. The default implementation does nothing.
	 * 
	 * @param e
	 */
	public void handleEvent(PortletEvent e) {
		// no-op
	}

	/**
	 * called immediately before a portlet is removed from the ui. If you need
	 * to do any cleanup (persisting of data, for instance) before the portlet
	 * is closed, do it here. If the method returns false, then the close will
	 * be aborted. Subclasses should override this if they want to do anything
	 * prior to removing the portlet from the UI. The default implementation
	 * does nothing.
	 */
	protected boolean getReadyForRemove() {
		return true;
	}

	/**
	 * handles the response to the click of the "configure" button. Subclasses
	 * should override this method if they have specific configuration they want
	 * to allow.The default implementation does nothing.
	 */
	protected void handleConfigClick() {
	}

	/**
	 * handles the response to the click of the "export snapshot" button.
	 * Subclasses should override this method if they are going to support
	 * snapshots as this default implementation does nothing.
	 */
	protected void handleExportClick() {

	}

	public int getFullscreenHeight() {
		return FULLSCREEN_HEIGHT;
	}

	public int getFullscreenWidth() {
		return FULLSCREEN_WIDTH;
	}

	public abstract String getName();

	public Widget getHeaderWidget() {
		return headerWidget;
	}

}
