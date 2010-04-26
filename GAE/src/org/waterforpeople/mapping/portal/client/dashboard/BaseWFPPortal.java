package org.waterforpeople.mapping.portal.client.dashboard;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.portal.client.widgets.PortletFactory;

import com.gallatinsystems.framework.gwt.portlet.client.PortalContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * base class for all WFP portal containers. this class unifies rendering of the
 * header logo/menu
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class BaseWFPPortal extends PortalContainer {
	private static final String CSS_SYSTEM_HEAD = "sys-header";
	private static final String ADD_ICON = "images/add-icon.png";
	private static final String ADD_TOOLTIP = "Add portlets to dashboard";

	private UserDto currentUser;
	

	public BaseWFPPortal(int columns) {
		super(columns);
	}

	/**
	 * constructs the header menu and binds the click listener for the
	 * "addWidget" button
	 * 
	 * @return
	 */
	protected Widget constructMenu(boolean isConfigurable) {
		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.add(new Image("images/WFP_Logo.png"));
		DockPanel statusDock = new DockPanel();
		statusDock.setPixelSize(1024, 20);
		statusDock.setStyleName(CSS_SYSTEM_HEAD);
		statusDock.add(new Label("Monitoring Dashboard"), DockPanel.WEST);
		statusDock.add(new SimplePanel(), DockPanel.CENTER);
		if (isConfigurable) {
			Image confImage = new Image(ADD_ICON);
			confImage.setTitle(ADD_TOOLTIP);
			confImage.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					new ConfigurationDialog().show();

				}
			});
			statusDock.add(confImage, DockPanel.EAST);
		}
		menuPanel.add(statusDock);
		return menuPanel;
	}

	/**
	 * Renders the dashboard configuration UI as a dialog box. From this box,
	 * the user will be able to select widgets to add to the system.
	 * 
	 * @author Christopher Fagiani
	 * 
	 */
	private class ConfigurationDialog extends DialogBox implements ClickHandler {

		private static final String HEADER_CSS = "tableheader";

		public ConfigurationDialog() {
			// Set the dialog box's caption.
			setText("Add Items to Dashboard");
			setAnimationEnabled(true);
			setGlassEnabled(true);

			VerticalPanel contentPane = new VerticalPanel();
			contentPane
					.add(new Label(
							"Select the portlets you want to add to your dashboard screen"));
			setPopupPosition(Window.getClientWidth() / 4, Window
					.getClientHeight() / 4);
			Grid g = new Grid(PortletFactory.AVAILABLE_PORTLETS.length + 1, 3);

			Label header = new Label("Portlets");
			header.setStyleName(HEADER_CSS);
			g.setWidget(0, 0, header);
			header = new Label("Description");
			header.setStyleName(HEADER_CSS);
			g.setWidget(0, 1, header);

			for (int i = 0; i < PortletFactory.AVAILABLE_PORTLETS.length; i++) {
				g.setText(i + 1, 0,
						(String) PortletFactory.AVAILABLE_PORTLETS[i][0]);
				g.setText(i + 1, 1,
						(String) PortletFactory.AVAILABLE_PORTLETS[i][1]);
				Image img = new Image(ADD_ICON);
				img.setTitle((String) PortletFactory.AVAILABLE_PORTLETS[i][0]);
				img.addClickHandler(this);
				g.setWidget(i + 1, 2, img);
			}
			g.getCellFormatter().setWidth(0, 2, "256px");
			contentPane.add(g);
			// DialogBox is a SimplePanel, so you have to set its widget
			// property to
			// whatever you want its contents to be.
			Button ok = new Button("Done");
			contentPane.add(ok);
			ok.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					ConfigurationDialog.this.hide();
				}
			});
			setWidget(contentPane);

		}

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource() instanceof Image) {
				Image img = (Image) event.getSource();
				String name = img.getTitle();
				addPortlet(PortletFactory.createPortlet(name, currentUser), 0,
						true);
				updateLayout();
			}
		}
	}

	/**
	 * no-op implementation. Subclasses should override this if they support
	 * saving layout information
	 */
	@Override
	protected void updateSavedLayout(Map<String, String> positionMap) {
		// no-op.

	}

	public UserDto getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserDto currentUser) {
		this.currentUser = currentUser;
	}

}
