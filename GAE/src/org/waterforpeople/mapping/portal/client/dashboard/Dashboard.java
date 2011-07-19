package org.waterforpeople.mapping.portal.client.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationItemDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationService;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.AccessPointManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.ActivityChartPortlet;
import org.waterforpeople.mapping.portal.client.widgets.ActivityMapPortlet;
import org.waterforpeople.mapping.portal.client.widgets.AdminWizardPortlet;
import org.waterforpeople.mapping.portal.client.widgets.DataUploadPortlet;
import org.waterforpeople.mapping.portal.client.widgets.DeviceFileManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.DisplayContentManager;
import org.waterforpeople.mapping.portal.client.widgets.MappingAttributeManager;
import org.waterforpeople.mapping.portal.client.widgets.MessageViewPortlet;
import org.waterforpeople.mapping.portal.client.widgets.MetricManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.PortletFactory;
import org.waterforpeople.mapping.portal.client.widgets.RawDataViewPortlet;
import org.waterforpeople.mapping.portal.client.widgets.RemoteExceptionPortlet;
import org.waterforpeople.mapping.portal.client.widgets.RunReportsPortlet;
import org.waterforpeople.mapping.portal.client.widgets.StandardScoringManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SummaryPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SurveyAssignmentPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SurveyAttributeMappingPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SurveyLoaderPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SurveyManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SurveyQuestionPortlet;
import org.waterforpeople.mapping.portal.client.widgets.TechnologyTypeManagerPortlet;
import org.waterforpeople.mapping.portal.client.widgets.UserManagerPortlet;

import com.gallatinsystems.framework.gwt.portlet.client.PortalContainer;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.PermissionConstants;
import com.gallatinsystems.user.app.gwt.client.UserConfigDto;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.app.gwt.client.UserService;
import com.gallatinsystems.user.app.gwt.client.UserServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final String CSS_SYSTEM_HEAD = "sys-header";
	private static final String ADD_ICON = "images/add-icon.png";
	private static final String ADD_TOOLTIP = TEXT_CONSTANTS.addToDashboard();
	private static final String DEFAULT_DOMAIN_TYPE = "accessPoint";
	private static final String DOMAIN_CONFIG_KEY = "domainType";

	private static final String LOCALE_DOMAIN = "locale";
	private UserDto currentUser;
	private VerticalPanel containerPanel;
	private Image confImage;
	private Panel menuPanel;
	private String domainType;

	public Dashboard() {
		super(COLUMNS);
	}

	public void onModuleLoad() {
		ConfigurationServiceAsync cfgService = GWT
				.create(ConfigurationService.class);
		cfgService.getConfigurationItem(DOMAIN_CONFIG_KEY,
				new AsyncCallback<ConfigurationItemDto>() {

					@Override
					public void onFailure(Throwable caught) {
						domainType = DEFAULT_DOMAIN_TYPE;
						completeInitialization();
					}

					@Override
					public void onSuccess(ConfigurationItemDto result) {
						if (result == null || result.getValue() == null) {
							domainType = DEFAULT_DOMAIN_TYPE;
						} else {
							domainType = result.getValue();
						}
						completeInitialization();
					}
				});
	}

	public void completeInitialization() {
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle()
				.setProperty("position", "relative");

		containerPanel = new VerticalPanel();

		menuPanel = constructMenu();
		RootPanel.get().add(containerPanel);
		containerPanel.add(menuPanel);
		menuPanel.setVisible(false);
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
					Anchor logoutAnchor = new Anchor(
							TEXT_CONSTANTS.logOnAsDifferentUser(),
							result.getLogoutUrl());
					containerPanel.add(logoutAnchor);
				}
				if (result != null && result.hasAccess()) {

					setCurrentUser(result);
					populateMenuPanel(true);
					menuPanel.setVisible(true);
					initializeContent(result);
				} else if (result == null || !result.hasAccess()) {
					containerPanel.clear();
					containerPanel.add(new Image("images/wfp-logo.gif"));
					MessageDialog errDia = new MessageDialog(
							TEXT_CONSTANTS.unknownUser(),
							TEXT_CONSTANTS.noFlowAccess());
					errDia.show();
				}
			}
		};
		userService.getCurrentUserConfig(false, userCallback);
	}

	protected void hideMenuItems() {

	}

	/**
	 * populates menu optiosn and binds click listeners for addWidget button
	 * 
	 * @param isConfigurable
	 */
	protected void populateMenuPanel(boolean isConfigurable) {
		DockPanel menuDock = new DockPanel();
		menuDock.setPixelSize(1024, 20);
		menuDock.setStyleName(CSS_SYSTEM_HEAD);
		MenuBar menu = new MenuBar();

		menu.addItem(TEXT_CONSTANTS.dashboard(), new Command() {
			public void execute() {
				exitFullscreen();
				if (confImage != null) {
					confImage.setVisible(true);
				}
			}
		});

		MenuBar mgrMenu = new MenuBar(true);
		mgrMenu.setAnimationEnabled(true);
		mgrMenu.addItem(TEXT_CONSTANTS.accessPoint(), new Command() {
			public void execute() {
				launchFullscreen(AccessPointManagerPortlet.NAME);
			}
		});

		if (getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			mgrMenu.addItem(TEXT_CONSTANTS.surveyAssignmentPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(SurveyAssignmentPortlet.NAME);
						}
					});
			mgrMenu.addItem(
					TEXT_CONSTANTS.surveyAttributeMappingPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(SurveyAttributeMappingPortlet.NAME);

						}
					});
			mgrMenu.addItem(TEXT_CONSTANTS.surveyLoaderPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(SurveyLoaderPortlet.NAME);
						}
					});

			mgrMenu.addItem(TEXT_CONSTANTS.technologyTypeManagerPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(TechnologyTypeManagerPortlet.NAME);
						}
					});
			mgrMenu.addItem(
					TEXT_CONSTANTS.standardScoringManagerPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(StandardScoringManagerPortlet.NAME);
						}
					});
		}
		if (getCurrentUser().hasPermission(PermissionConstants.EDIT_EDITORIAL)) {
			mgrMenu.addItem(TEXT_CONSTANTS.displayContentManagerTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(DisplayContentManager.NAME);

						}
					});
		}

		mgrMenu.addItem(TEXT_CONSTANTS.surveyManagerPortletTitle(),
				new Command() {
					public void execute() {
						launchFullscreen(SurveyManagerPortlet.NAME);

					}
				});
		if (getCurrentUser().hasPermission(
				PermissionConstants.UPLOAD_SURVEY_DATA)) {
			mgrMenu.addItem(TEXT_CONSTANTS.uploadPortletTitle(), new Command() {
				public void execute() {
					launchFullscreen(DataUploadPortlet.NAME);
				}
			});
		}

		mgrMenu.addItem(TEXT_CONSTANTS.rawDataViewPortletName(), new Command() {
			public void execute() {
				launchFullscreen(RawDataViewPortlet.NAME);
			}
		});
		if (getCurrentUser().hasPermission(PermissionConstants.IMPORT_AP_DATA)) {
			mgrMenu.addItem(TEXT_CONSTANTS.mappingAttributeManagerTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(MappingAttributeManager.NAME);
						}
					});
		}
		if (getCurrentUser().hasPermission(PermissionConstants.EDIT_USER)) {
			mgrMenu.addItem(TEXT_CONSTANTS.userManagerPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(UserManagerPortlet.NAME);
						}
					});
		}
		if (getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			mgrMenu.addItem(TEXT_CONSTANTS.adminWizardPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(AdminWizardPortlet.NAME);

						}
					});

		}
		if (getCurrentUser().isAdmin()) {
			mgrMenu.addItem(TEXT_CONSTANTS.remoteExceptionPortletTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(RemoteExceptionPortlet.NAME);
						}
					});
			mgrMenu.addItem(TEXT_CONSTANTS.deviceFileManagerTitle(),
					new Command() {
						public void execute() {
							launchFullscreen(DeviceFileManagerPortlet.NAME);

						}
					});
			if (LOCALE_DOMAIN.equalsIgnoreCase(domainType)) {
				mgrMenu.addItem(TEXT_CONSTANTS.metricManager(), new Command() {
					public void execute() {
						launchFullscreen(MetricManagerPortlet.NAME);
					}
				});
			}
		}

		menu.addItem(TEXT_CONSTANTS.dataManagers(), mgrMenu);
		if (getCurrentUser().hasPermission(PermissionConstants.RUN_REPORTS)) {
			menu.addItem(TEXT_CONSTANTS.runReports(), new Command() {

				@Override
				public void execute() {
					launchFullscreen(RunReportsPortlet.NAME);

				}
			});
		}

		if (getCurrentUser().hasPermission(PermissionConstants.VIEW_MESSAGES)) {
			menu.addItem(TEXT_CONSTANTS.viewMessages(), new Command() {
				@Override
				public void execute() {
					launchFullscreen(MessageViewPortlet.NAME);

				}
			});
		}

		menuDock.add(menu, DockPanel.WEST);
		menuDock.add(new SimplePanel(), DockPanel.CENTER);
		if (isConfigurable) {
			confImage = new Image(ADD_ICON);
			confImage.setTitle(ADD_TOOLTIP);
			confImage.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					new ConfigurationDialog().show();

				}
			});
			menuDock.add(confImage, DockPanel.EAST);
		}
		menuPanel.add(menuDock);
	}

	/**
	 * constructs the header menu bar
	 * 
	 * @return
	 */
	protected Panel constructMenu() {
		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.add(new Image("images/wfp-logo.gif"));
		return menuPanel;
	}

	/**
	 * launches a portlet in fullscreen mode and hides unneeded controls
	 * 
	 * @param portletName
	 */
	private void launchFullscreen(String portletName) {
		Portlet p = PortletFactory.createPortlet(portletName, getCurrentUser(),
				domainType);
		p.setShowFullscreen(true);
		if (confImage != null) {
			confImage.setVisible(false);
		}
		takeoverScreen(p);
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
			installDefaultPortlets();
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
			int count = 0;
			for (int i = 0; i < COLUMNS; i++) {
				Object[] key = colMap.get(i).keySet().toArray();
				if (key.length > 0) {
					Arrays.sort(key);
					for (int j = 0; j < key.length; j++) {
						try {
							addPortlet(PortletFactory.createPortlet(
									colMap.get(i).get(key[j]),
									getCurrentUser(), domainType), i, true);
							count++;
						} catch (IllegalArgumentException e) {
							// swallow in case we change portlet names and don't
							// update the DB
						}
					}
				}
			}
			if (count == 0) {
				installDefaultPortlets();
			}
		}
	}

	private void installDefaultPortlets() {
		Map<String, String> posMap = new HashMap<String, String>();

		addPortlet(new SummaryPortlet(), 0, true);
		posMap.put(SummaryPortlet.NAME, "0,0");

		addPortlet(new ActivityChartPortlet(getCurrentUser()), 1, true);
		posMap.put(ActivityChartPortlet.NAME, "1,0");

		addPortlet(new SurveyQuestionPortlet(), 2, true);
		posMap.put(SummaryPortlet.NAME, "2,0");

		// if this is the first time the user logged in, create a config for
		// him with the default portlet set
		updateUserConfig(posMap);

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
		private static final String DIALOG_CONTENT_CSS = "dialogContent";

		public ConfigurationDialog() {
			// Set the dialog box's caption.
			setText(TEXT_CONSTANTS.addItemToDashboard());
			setAnimationEnabled(true);
			setGlassEnabled(true);

			VerticalPanel contentPane = new VerticalPanel();
			contentPane.add(new Label(TEXT_CONSTANTS.selectPortlets()));
			setPopupPosition(Window.getClientWidth() / 4,
					Window.getClientHeight() / 4);
			Grid g = new Grid(PortletFactory.AVAILABLE_PORTLETS.length + 1, 3);
			g.setStyleName(DIALOG_CONTENT_CSS);

			Label header = new Label(TEXT_CONSTANTS.portlets());
			header.setStyleName(HEADER_CSS);
			g.setWidget(0, 0, header);
			header = new Label(TEXT_CONSTANTS.description());
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
			Button ok = new Button(TEXT_CONSTANTS.done());
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
				addPortlet(PortletFactory.createPortlet(name, currentUser,
						domainType), 0, true);
				updateLayout();
			}
		}

		@Override
		public boolean onKeyDownPreview(char key, int modifiers) {
			switch (key) {
			case KeyCodes.KEY_ESCAPE:
				hide();
				return true;
			}
			return false;
		}
	}
}
