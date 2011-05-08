package com.gallatinsystems.notification.app.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for retrieving/saving notificationsubscription objects
 * 
 * @author Christopher Fagiani
 *
 */
@RemoteServiceRelativePath("notificationsubrpcservice")
public interface NotificationSubscriptionService extends RemoteService{

	public List<NotificationSubscriptionDto> listSubscriptions(Long entityId, String type);
	public List<NotificationSubscriptionDto> saveSubscriptions(List<NotificationSubscriptionDto> dtoList);
	public void deleteSubscription(NotificationSubscriptionDto dto);
	
}
