package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
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
public class NotificationSubscriptionWidget extends Composite {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	private static final String ATTACHMENT_OPTION = "Attachment";
	private static final String LINK_OPTION = "Link";
	private HorizontalPanel horizPanel;
	private NotificationSubscriptionDto subscription;
	private TextBox emailBox;
	private ListBox optionSelector;
	private DateBox expiryBox;

	public NotificationSubscriptionWidget(NotificationSubscriptionDto dto) {
		subscription = dto;
		horizPanel = new HorizontalPanel();
		emailBox = new TextBox();
		optionSelector = new ListBox(false);
		optionSelector.addItem(TEXT_CONSTANTS.attachment(), ATTACHMENT_OPTION
				.toUpperCase());
		optionSelector.addItem(TEXT_CONSTANTS.link(), LINK_OPTION.toUpperCase());
		expiryBox = new DateBox();
		expiryBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		ViewUtil.installFieldRow(horizPanel,TEXT_CONSTANTS.email(), emailBox, null);
		ViewUtil.installFieldRow(horizPanel, TEXT_CONSTANTS.type(), optionSelector, null);
		ViewUtil.installFieldRow(horizPanel, TEXT_CONSTANTS.expires(), expiryBox, null);

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
			if (subscription.getNotificationOption() != null) {
				ViewUtil.setListboxSelection(optionSelector, subscription
						.getNotificationOption());
			} else {
				optionSelector.setSelectedIndex(0);
			}	
		} else {
			subscription = new NotificationSubscriptionDto();
			subscription.setNotificationMethod("EMAIL");
		}
	}

	public NotificationSubscriptionDto getValue() {
		if (subscription != null) {
			subscription.setNotificationDestination(emailBox.getText().trim());
			subscription.setExpiryDate(expiryBox.getValue());
			subscription.setNotificationMethod("EMAIL");
			subscription.setNotificationOption(optionSelector
					.getValue(optionSelector.getSelectedIndex()));
		}
		return subscription;
	}

}
