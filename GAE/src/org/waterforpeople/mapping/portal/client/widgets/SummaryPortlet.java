/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.Collections;

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
	public static final String DESCRIPTION = TEXT_CONSTANTS
			.summaryPortletDescription();
	public static final String NAME = TEXT_CONSTANTS.summaryPortletTitle();
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final String TREE_ITEM_HEIGHT = "25";
	private static final String USER_IMAGE = "images/users.png";
	//private static final String GOOGLE_EARTH_IMAGE = "images/google_earth_icon.png";
	private static final String SURVEY_IMAGE = "images/surveys.png";
	private static final String DEVICE_IMAGE = "images/device.png";
	private TreeItem surveyRoot;
	@SuppressWarnings("unused")
	private SurveyTree surveyTree;
	private TreeItem deviceRoot;
	private TreeItem userRoot;
	@SuppressWarnings("unused")
	private TreeItem kmlRoot;

	Tree t = null;

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
					//Sort list alphabetically
					ArrayList<String> nl = new ArrayList<String>(result.length);
					for (int i = 0; i < result.length; i++) {
						nl.add(result[i].getPhoneNumber());
					}
					Collections.sort(nl);
					for (int i = 0; i < nl.size(); i++) {
						TreeItem tItem = new TreeItem(nl.get(i));
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
					//Sort list alphabetically
					ArrayList<String> ul = new ArrayList<String>(result.length);
					for (int i = 0; i < result.length; i++) {
						if (result[i].getUserName() != null) {
							ul.add(result[i].getUserName());
						} else {
							ul.add(result[i].getEmailAddress());
						}
					}
					Collections.sort(ul);
					for (int i = 0; i < ul.size(); i++) {
						TreeItem tItem = new TreeItem(ul.get(i));
						userRoot.addItem(tItem);

					}

				}
			}
		};
		userService.listUser(userCallback);
		setContent(constructTree());
	}

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

		// loadCountryMapLinks(t);

		return t;
	}
	

	public String getName() {
		return NAME;
	}

}
