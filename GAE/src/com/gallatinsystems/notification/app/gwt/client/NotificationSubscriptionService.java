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
public interface NotificationSubscriptionService extends RemoteService {

	/**
	 * lists all subscriptions for a given entity id/type combination
	 * 
	 * @param entityId
	 * @param type
	 * @return
	 */
	public List<NotificationSubscriptionDto> listSubscriptions(Long entityId,
			String type);

	/**
	 * saves a subscription
	 * 
	 * @param dtoList
	 * @return
	 */
	public List<NotificationSubscriptionDto> saveSubscriptions(
			List<NotificationSubscriptionDto> dtoList);

	/**
	 * deletes a subscription
	 * 
	 * @param dto
	 */
	public void deleteSubscription(NotificationSubscriptionDto dto);

}
