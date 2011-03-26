package org.waterforpeople.mapping.portal.client.widgets;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.view.SurveyTree;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.app.gwt.client.UserService;
import com.gallatinsystems.user.app.gwt.client.UserServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Displays summary information
 * 
 * @author Christopher Fagiani
 * 
 */
public class SummaryPortlet extends Portlet {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String DESCRIPTION =TEXT_CONSTANTS.summaryPortletDescription();
	public static final String NAME = TEXT_CONSTANTS.summaryPortletTitle();
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final String TREE_ITEM_HEIGHT = "25";
	private static final String USER_IMAGE = "images/users.png";
	private static final String GOOGLE_EARTH_IMAGE = "images/google_earth_icon.png";
	private static final String SURVEY_IMAGE = "images/surveys.png";
	private static final String DEVICE_IMAGE = "images/device.png";
	private TreeItem surveyRoot;
	@SuppressWarnings("unused")
	private SurveyTree surveyTree;
	private TreeItem deviceRoot;
	private TreeItem userRoot;
	@SuppressWarnings("unused")
	private TreeItem kmlRoot;

	public SummaryPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);

		DeviceServiceAsync deviceService = GWT.create(DeviceService.class);
		// Set up the callback object.
		AsyncCallback<DeviceDto[]> deviceCallback = new AsyncCallback<DeviceDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(DeviceDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						TreeItem tItem = new TreeItem(result[i]
								.getPhoneNumber());
						deviceRoot.addItem(tItem);

					}
				}
			}
		};
		deviceService.listDevice(deviceCallback);

		UserServiceAsync userService = GWT.create(UserService.class);
		// Set up the callback object.
		AsyncCallback<UserDto[]> userCallback = new AsyncCallback<UserDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(UserDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						if (result[i].getUserName() != null) {
							userRoot.addItem(result[i].getUserName());
						} else {
							userRoot.addItem(result[i].getEmailAddress());
						}
					}
				}
			}
		};
		userService.listUser(userCallback);
		setContent(constructTree());
	}
	Tree t = null;
	private Tree constructTree() {
		t = new Tree();

		HorizontalPanel panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(SURVEY_IMAGE));
		panel.add(new Label(TEXT_CONSTANTS.surveys()));
		surveyRoot = t.addItem(panel);
		surveyTree = new SurveyTree(surveyRoot, null, false);
		panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(USER_IMAGE));
		panel.add(new Label(TEXT_CONSTANTS.users()));
		userRoot = t.addItem(panel);

		panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(DEVICE_IMAGE));
		panel.add(new Label(TEXT_CONSTANTS.devices()));
		deviceRoot = t.addItem(panel);

		loadCountryMapLinks(t);

		return t;
	}

	private void loadCountryMapLinks(Tree t){
		AccessPointManagerServiceAsync apService = GWT.create(AccessPointManagerService.class);
		AsyncCallback<List<String>> countryCallback = new AsyncCallback<List<String>>(){

			@Override
			public void onFailure(Throwable caught) {
				// no-op
				
			}

			@Override
			public void onSuccess(List<String> result) {
				for(String countryCode :result)
				addCountryMapOption(countryCode);
			}
		};
		apService.listCountryCodes(countryCallback);
			
		
	}
String currentCountryCode = null;
	private HorizontalPanel addCountryMapOption(String countryCode) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(GOOGLE_EARTH_IMAGE));
		Label l = new Label(TEXT_CONSTANTS.viewCurrentMapFor()+": " + countryCode);
		currentCountryCode = countryCode;
		l.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String labelText = ((Label)event.getSource()).getText();
				String countryCode = labelText.split(":")[1].trim();
				Window.open("/webapp/waterforpeoplemappinggoogle?showKML=true&countryCode=" + countryCode,
						"KMZ", null);
			}
		});
		panel.add(l);
		kmlRoot = t.addItem(panel);
		return panel;
	}

	public String getName() {
		return NAME;
	}

}
