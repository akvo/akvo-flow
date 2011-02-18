package org.waterforpeople.mapping.portal.client.widgets.component;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
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
		optionSelector.addItem(ATTACHMENT_OPTION, ATTACHMENT_OPTION
				.toUpperCase());
		optionSelector.addItem(LINK_OPTION, LINK_OPTION.toUpperCase());
		expiryBox = new DateBox();
		expiryBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		ViewUtil.installFieldRow(horizPanel, "Email:", emailBox, null);
		ViewUtil.installFieldRow(horizPanel, "Type:", optionSelector, null);
		ViewUtil.installFieldRow(horizPanel, "Expires", expiryBox, null);

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
			subscription = new NotificationSubscriptionDto();
		} else {
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
