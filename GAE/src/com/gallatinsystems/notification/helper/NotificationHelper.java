package com.gallatinsystems.notification.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationSubscription;

/**
 * Finds current notification subscriptions from the data store and spawns async
 * jobs to generate data and send the notification
 * 
 * @author Christopher Fagiani
 */
public class NotificationHelper {
	private NotificationSubscriptionDao notificationDao;

	public NotificationHelper() {
		notificationDao = new NotificationSubscriptionDao();
	}

	public void execute() {
		// find all notifications that have not yet expired
		List<NotificationSubscription> subs = notificationDao
				.listUnexpiredNotifications();
		// group the subs by entity id and type
		Map<String, Map<Long, List<NotificationSubscription>>> subMap = collateSubscriptions(subs);
		if (subMap != null) {
			// now spawn a notification job for each notificationType/entity
			// combo
			for (Entry<String, Map<Long, List<NotificationSubscription>>> entry : subMap
					.entrySet()) {
				// TODO add queue params

			}

		}
	}

	private Map<String, Map<Long, List<NotificationSubscription>>> collateSubscriptions(
			List<NotificationSubscription> subs) {
		Map<String, Map<Long, List<NotificationSubscription>>> subMap = new HashMap<String, Map<Long, List<NotificationSubscription>>>();
		if (subs != null) {
			for (NotificationSubscription sub : subs) {
				Map<Long, List<NotificationSubscription>> tempMap = subMap
						.get(sub.getNotificationType());
				if (tempMap == null) {
					tempMap = new HashMap<Long, List<NotificationSubscription>>();
					subMap.put(sub.getNotificationType(), tempMap);
				}
				List<NotificationSubscription> tempList = tempMap.get(sub
						.getEntityId());
				if (tempList == null) {
					tempList = new ArrayList<NotificationSubscription>();
					tempMap.put(sub.getEntityId(), tempList);
				}
				tempList.add(sub);
			}
		}
		return subMap;
	}
}