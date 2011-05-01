package com.gallatinsystems.notification.app.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NotificationSubscriptionServiceAsync {

	void listSubscriptions(Long entityId, String type,
			AsyncCallback<List<NotificationSubscriptionDto>> callback);

	void saveSubscriptions(List<NotificationSubscriptionDto> dtoList,
			AsyncCallback<List<NotificationSubscriptionDto>> callback);

	void deleteSubscription(NotificationSubscriptionDto dto,
			AsyncCallback<Void> callback);

}
