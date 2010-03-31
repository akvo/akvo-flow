package org.waterforpeople.mapping.portal.client.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.user.UserConfigDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;
import org.waterforpeople.mapping.app.gwt.client.user.UserServiceAsync;
import org.waterforpeople.mapping.portal.client.widgets.ActivityChartPortlet;
import org.waterforpeople.mapping.portal.client.widgets.ActivityMapPortlet;
import org.waterforpeople.mapping.portal.client.widgets.PortletFactory;
import org.waterforpeople.mapping.portal.client.widgets.SummaryPortlet;

import com.gallatinsystems.framework.gwt.portlet.client.PortalContainer;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Main container for the dashboard.
 * 
 * TODO: make this load the list of widgets and their relative position/order
 * from the datastore on a per-user basis
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class Dashboard extends PortalContainer implements EntryPoint {
	private static final int COLUMNS = 3;
	private static final String CONFIG_GROUP = "DASHBOARD";
	private static final String CSS_SYSTEM_HEAD = "sys-header";
	private static final String ADD_ICON = "images/add-icon.png";

	private VerticalPanel containerPanel;
	private UserDto currentUser;

	public Dashboard() {
		super(COLUMNS);
	}

	public void onModuleLoad() {
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle().setProperty("position",
				"relative");

		containerPanel = new VerticalPanel();

		containerPanel.add(constructMenu());
		RootPanel.get().add(containerPanel);

		// get the user config
		UserServiceAsync userService = GWT.create(UserService.class);
		// Set up the callback object.
		AsyncCallback<UserDto> userCallback = new AsyncCallback<UserDto>() {
			public void onFailure(Throwable caught) {
				initializeContent(null);
			}

			public void onSuccess(UserDto result) {
				if (result != null) {
					currentUser = result;
					initializeContent(result);
				} else {
					initializeContent(null);
				}
			}
		};
		userService.getCurrentUserConfig(userCallback);
	}

	/**
	 * constructs the header menu and binds the click listener for the
	 * "addWidget" button
	 * 
	 * @return
	 */
	private Widget constructMenu() {
		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.add(new Image("images/WFP_Logo.png"));
		DockPanel statusDock = new DockPanel();
		statusDock.setPixelSize(1024, 20);
		statusDock.setStyleName(CSS_SYSTEM_HEAD);
		statusDock.add(new Label("Monitoring Dashboard"), DockPanel.WEST);
		statusDock.add(new SimplePanel(), DockPanel.CENTER);
		Image confImage = new Image(ADD_ICON);
		confImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new ConfigurationDialog().show();

			}
		});

		statusDock.add(confImage, DockPanel.EAST);
		menuPanel.add(statusDock);
		return menuPanel;
	}

	/**
	 * If the user has a configuration set, this will render the dashboard using
	 * his saved settings. If not, it will layout the standard set of widgets
	 * and then create a config record in the datastore for them.
	 * 
	 * @param user
	 */
	private void initializeContent(UserDto user) {
		if (user == null || user.getConfig() == null
				|| user.getConfig().get(CONFIG_GROUP) == null
				|| user.getConfig().get(CONFIG_GROUP).size() == 0) {
			Map<String, String> posMap = new HashMap<String, String>();

			addPortlet(new SummaryPortlet(), 0, true);
			posMap.put(SummaryPortlet.NAME, "0,0");

			addPortlet(new ActivityChartPortlet(), 1, true);
			posMap.put(ActivityChartPortlet.NAME, "1,0");

			addPortlet(new ActivityMapPortlet(), 1, true);
			posMap.put(ActivityMapPortlet.NAME, "1,1");

			updateUserConfig(posMap);

		} else {
			List<Map<Integer, String>> colMap = new ArrayList<Map<Integer, String>>();
			for (int i = 0; i < COLUMNS; i++) {
				colMap.add(new HashMap<Integer, String>());
			}
			// build up the list of widgets and their positions
			for (UserConfigDto config : user.getConfig().get(CONFIG_GROUP)) {
				String val = config.getValue();
				Integer row = 0;
				Integer col = 0;
				if (val.contains("\n")) {
					String[] coords = val.substring(0, val.indexOf("\n"))
							.trim().split(",");
					if (coords.length == 2) {
						col = new Integer(coords[0]);
						row = new Integer(coords[1]);
					}
					colMap.get(col).put(row, config.getName());
				}
			}
			// now install the portlets in the right order
			for (int i = 0; i < COLUMNS; i++) {
				Object[] key = colMap.get(i).keySet().toArray();
				if (key.length > 0) {
					Arrays.sort(key);
					for (int j = 0; j < key.length; j++) {
						try {
							addPortlet(PortletFactory.createPortlet(colMap.get(
									i).get(key[j])), i, true);
						} catch (IllegalArgumentException e) {
							// swallow in case we change portlet names and don't
							// update the DB
						}
					}
				}
			}
		}
		// now add the portal container to the vertical panel
		containerPanel.add(this);
	}

	@Override
	public Class<?>[] getInvolvedClasses() {
		return new Class[] { this.getClass(), SummaryPortlet.class,
				ActivityChartPortlet.class, ActivityMapPortlet.class };
	}

	/**
	 * persists the layout information to the server
	 * 
	 * @param positionMap
	 */
	protected void updateUserConfig(Map<String, String> positionMap) {
		Map<String, Set<UserConfigDto>> confMap = currentUser.getConfig();
		UserServiceAsync userService = GWT.create(UserService.class);
		// Set up the callback object.
		AsyncCallback<Void> userCallback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// no-op
			}

			@Override
			public void onSuccess(Void result) {
				// no-op
			}
		};
		if (confMap == null) {
			confMap = new HashMap<String, Set<UserConfigDto>>();
			currentUser.setConfig(confMap);
		}

		for (String item : positionMap.keySet()) {
			Set<UserConfigDto> existingConf = confMap.get(CONFIG_GROUP);
			if (existingConf == null) {
				existingConf = new HashSet<UserConfigDto>();
				confMap.put(CONFIG_GROUP, existingConf);
			}
			boolean found = false;
			for(UserConfigDto confItem: existingConf){
				if (confItem.getName() != null
						&& confItem.getName().equals(item)) {
					confItem.setValue(positionMap.get(item) + "\n");
				}	
			}
			
			if (!found) {
				UserConfigDto confDto = new UserConfigDto();
				confDto.setGroup(CONFIG_GROUP);
				confDto.setName(item);
				confDto.setValue(positionMap.get(item) + "\n");
				existingConf.add(confDto);
			}
		}
		userService.saveUser(currentUser, userCallback);
	}

	@Override
	protected void updateSavedLayout(Map<String, String> positionMap) {
		updateUserConfig(positionMap);
	}

	/**
	 * Renders the dashboard configuration UI as a dialog box. From this box,
	 * the user will be able to select widgets to add to the system.
	 * 
	 * @author Christopher Fagiani
	 * 
	 */
	private class ConfigurationDialog extends DialogBox implements ClickHandler {

		public ConfigurationDialog() {
			// Set the dialog box's caption.
			setText("Add Items to Dashboard");
			setAnimationEnabled(true);
			setGlassEnabled(true);

			VerticalPanel contentPane = new VerticalPanel();
			contentPane
					.add(new Label(
							"Select the portlets you want to add to your dashboard screen"));
			setPopupPosition(Window.getClientWidth() / 3, Window
					.getClientHeight() / 3);
			Grid g = new Grid(PortletFactory.AVAILABLE_PORTLETS.length + 1, 3);

			g.setText(0, 0, "Portlets");
			g.setText(0, 1, "Description");
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
				int position = addPortlet(PortletFactory.createPortlet(name),
						0, true);
				Map<String, String> posMap = new HashMap<String, String>();
				posMap.put(name, "0," + position);
				updateUserConfig(posMap);
			}
		}
	}

}
