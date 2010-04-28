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
import org.waterforpeople.mapping.portal.client.widgets.AccessPointManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.ActivityChartPortlet;
import org.waterforpeople.mapping.portal.client.widgets.ActivityMapPortlet;
import org.waterforpeople.mapping.portal.client.widgets.PortletFactory;
import org.waterforpeople.mapping.portal.client.widgets.SummaryPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SurveyQuestionPortlet;

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
	private static final String ADD_TOOLTIP = "Add portlets to dashboard";

	private UserDto currentUser;
	private VerticalPanel containerPanel;

	public Dashboard() {
		super(COLUMNS);
	}

	public void onModuleLoad() {
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle().setProperty("position",
				"relative");

		containerPanel = new VerticalPanel();

		containerPanel.add(constructMenu(true));
		RootPanel.get().add(containerPanel);
		// now add the portal container to the vertical panel
		containerPanel.add(this);
		// get the user config
		UserServiceAsync userService = GWT.create(UserService.class);
		// Set up the callback object.
		AsyncCallback<UserDto> userCallback = new AsyncCallback<UserDto>() {
			public void onFailure(Throwable caught) {
				initializeContent(null);
			}

			public void onSuccess(UserDto result) {
				if (result != null) {
					setCurrentUser(result);
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

			addPortlet(new ActivityChartPortlet(getCurrentUser()), 1, true);
			posMap.put(ActivityChartPortlet.NAME, "1,0");

			addPortlet(new ActivityMapPortlet(getCurrentUser()), 1, true);
			posMap.put(ActivityMapPortlet.NAME, "1,1");

			addPortlet(new SurveyQuestionPortlet(), 2, true);
			posMap.put(SummaryPortlet.NAME, "2,0");

			addPortlet(new AccessPointManagerPortlet(getCurrentUser()), 1, true);
			posMap.put(AccessPointManagerPortlet.NAME, "2,1");

			// if this is the first time the user logged in, create a config for
			// him with the default portlet set
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
				if (val != null) {
					String[] coords = val.trim().split(",");
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
									i).get(key[j]), getCurrentUser()), i, true);
						} catch (IllegalArgumentException e) {
							// swallow in case we change portlet names and don't
							// update the DB
						}
					}
				}
			}
		}
	}

	public UserDto getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserDto currentUser) {
		this.currentUser = currentUser;
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
		Map<String, Set<UserConfigDto>> confMap = getCurrentUser().getConfig();
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
			getCurrentUser().setConfig(confMap);
		}
		Set<UserConfigDto> groupConfig = new HashSet<UserConfigDto>();
		confMap.put(CONFIG_GROUP, groupConfig);
		for (String item : positionMap.keySet()) {
			UserConfigDto confDto = new UserConfigDto();
			confDto.setGroup(CONFIG_GROUP);
			confDto.setName(item);
			confDto.setValue(positionMap.get(item));
			groupConfig.add(confDto);
		}
		userService.saveUser(getCurrentUser(), userCallback);
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
}
