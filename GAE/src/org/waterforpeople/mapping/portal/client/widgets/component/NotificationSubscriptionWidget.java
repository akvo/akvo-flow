package org.waterforpeople.mapping.portal.client.widgets.component;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

	private HorizontalPanel horizPanel;
	private NotificationSubscriptionDto subscription;
	private TextBox emailBox;
	private DateBox expiryBox;

	public NotificationSubscriptionWidget(NotificationSubscriptionDto dto) {
		subscription = dto;
		horizPanel = new HorizontalPanel();
		emailBox = new TextBox();
		expiryBox = new DateBox();
		expiryBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		ViewUtil.installFieldRow(horizPanel, "Email:", emailBox, null);
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
		}
		return subscription;
	}

}
