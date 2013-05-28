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

package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * ui widget to display NotificationSubscription objects
 * 
 * TODO: support multiple notification Methods (right now, we only support
 * EMAIL)
 * 
 * @author Christopher Fagiani
 * 
 */
public class NotificationSubscriptionWidget extends Composite implements
		ChangeHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String ATTACHMENT_OPTION = "Attachment";
	private static final String LINK_OPTION = "Link";
	private HorizontalPanel horizPanel;
	private NotificationSubscriptionDto subscription;
	private ListBox typeSel;
	private TextBox emailBox;
	private ListBox optionSelector;
	private DateBox expiryBox;

	public NotificationSubscriptionWidget(NotificationSubscriptionDto dto) {
		subscription = dto;
		horizPanel = new HorizontalPanel();
		emailBox = new TextBox();
		optionSelector = new ListBox(false);
		typeSel = new ListBox(false);

		expiryBox = new DateBox();
		expiryBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getFormat(PredefinedFormat.DATE_SHORT)));

		ViewUtil.installFieldRow(horizPanel, TEXT_CONSTANTS.email(), emailBox,
				null);
		ViewUtil.installFieldRow(horizPanel, TEXT_CONSTANTS.event(), typeSel,
				null);
		ViewUtil.installFieldRow(horizPanel, TEXT_CONSTANTS.type(),
				optionSelector, null);
		ViewUtil.installFieldRow(horizPanel, TEXT_CONSTANTS.expires(),
				expiryBox, null);

		typeSel.addItem(TEXT_CONSTANTS.rawDataReportGeneration(),
				"rawDataReport");
		typeSel.addItem(TEXT_CONSTANTS.fieldStatusReportGeneration(),
				"fieldStatusReport");
		typeSel.addItem(TEXT_CONSTANTS.surveySubmission(), "surveySubmission");
		typeSel.addItem(TEXT_CONSTANTS.surveyApproval(), "surveyApproval");
		typeSel.addChangeHandler(this);

		initWidget(horizPanel);
		bindDataToUi();
	}

	protected void bindDataToUi() {
		if (subscription != null) {
			if (subscription.getNotificationDestination() != null) {
				emailBox.setText(subscription.getNotificationDestination());
			}
			if (subscription.getExpiryDate() != null) {
				expiryBox.setValue(subscription.getExpiryDate());
			}
			
			if (subscription.getNotificationType() != null) {
				ViewUtil.setListboxSelection(typeSel,
						subscription.getNotificationType());
			} else {
				typeSel.setSelectedIndex(0);
			}
			updateOptions();
			if (subscription.getNotificationOption() != null) {
				ViewUtil.setListboxSelection(optionSelector,
						subscription.getNotificationOption());
			} else {
				optionSelector.setSelectedIndex(0);
			}
		} else {
			subscription = new NotificationSubscriptionDto();
			subscription.setNotificationMethod("EMAIL");
			updateOptions();
		}
		
	}

	public NotificationSubscriptionDto getValue() {
		if (subscription != null) {
			subscription.setNotificationDestination(emailBox.getText().trim());
			subscription.setExpiryDate(expiryBox.getValue());
			subscription.setNotificationMethod("EMAIL");
			subscription.setNotificationOption(ViewUtil.getListBoxSelection(
					optionSelector, false));
			subscription.setNotificationType(ViewUtil.getListBoxSelection(
					typeSel, false));			

		}
		return subscription;
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == typeSel) {
			updateOptions();
		}
	}

	private void updateOptions() {
		optionSelector.clear();
		String type = ViewUtil.getListBoxSelection(typeSel, false);
		if ("rawDataReport".equals(type) || "fieldStatusReport".equals(type)) {
			optionSelector.addItem(TEXT_CONSTANTS.attachment(),
					ATTACHMENT_OPTION.toUpperCase());
			optionSelector.addItem(TEXT_CONSTANTS.link(),
					LINK_OPTION.toUpperCase());
		} else {
			optionSelector.addItem(TEXT_CONSTANTS.link(),
					LINK_OPTION.toUpperCase());
		}
	}

}
