package com.gallatinsystems.notification.helper;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gallatinsystems.notification.NotificationRequest;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationSubscription;
//import com.google.appengine.api.taskqueue.Queue;
//import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

/**
 * Finds current notification subscriptions from the data store and spawns async
 * jobs to generate data and send the notification.
 * 
 * To use this class you must define a queue called notification
 * 
 * @author Christopher Fagiani
 */
public class NotificationHelper {

	private static final String QUEUE_NAME = "notification";
	private static final String PROCESSOR_URL = "/notificationprocessor";
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
			Queue queue = QueueFactory.getQueue(QUEUE_NAME);
			for (Entry<String, Map<Long, List<NotificationSubscription>>> entry : subMap
					.entrySet()) {
				for (Entry<Long, List<NotificationSubscription>> notifEntry : entry
						.getValue().entrySet()) {
					StringBuilder builder = new StringBuilder();
					if (notifEntry.getValue() != null
							&& notifEntry.getValue().size() > 0) {
						for (int i = 0; i < notifEntry.getValue().size(); i++) {
							if (i > 0) {
								builder.append(NotificationRequest.DELIMITER);
							}
							builder.append(notifEntry.getValue().get(i)
									.getNotificationDestination());
						}
						// now dump the item on the queue
						/*queue.add(withUrl(PROCESSOR_URL).param(
								NotificationRequest.DEST_PARAM,
								builder.toString()).param(
										NotificationRequest.ENTITY_PARAM,
								notifEntry.getKey().toString()).param(
										NotificationRequest.TYPE_PARAM,
								entry.getKey()));*/
						queue.add(url(PROCESSOR_URL).param(
								NotificationRequest.DEST_PARAM,
								builder.toString()).param(
										NotificationRequest.ENTITY_PARAM,
								notifEntry.getKey().toString()).param(
										NotificationRequest.TYPE_PARAM,
								entry.getKey()));
					}
				}

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