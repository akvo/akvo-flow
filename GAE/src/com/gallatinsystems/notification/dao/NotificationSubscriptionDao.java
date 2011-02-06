package com.gallatinsystems.notification.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.notification.domain.NotificationHistory;
import com.gallatinsystems.notification.domain.NotificationSubscription;

/**
 * saves and finds NotificationSubscriptions from the datastore
 * 
 * @author Christopher Fagiani
 * 
 */
public class NotificationSubscriptionDao extends
		BaseDAO<NotificationSubscription> {

	public NotificationSubscriptionDao() {
		super(NotificationSubscription.class);
	}

	/**
	 * lists all unexpired notifications (where expiryDate <= sysdate)
	 * 
	 * @return
	 */
	public List<NotificationSubscription> listUnexpiredNotifications() {
		return listByProperty("expiryDate", new Date(), "Date", null, null,
				GTE_OP, NotificationSubscription.class);
	}

	/**
	 * lists all subscriptions
	 * 
	 * @param entityId
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NotificationSubscription> listSubscriptions(Long entityId,
			String type, boolean unexpiredOnly) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(NotificationSubscription.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("type", filterString, paramString, "String", type,
				paramMap);
		appendNonNullParam("entityId", filterString, paramString, "Long",
				entityId, paramMap);
		if (unexpiredOnly) {
			appendNonNullParam("expiryDate", filterString, paramString, "Date",
					new Date(), paramMap, GTE_OP);
			query.declareImports("import java.util.Date");
		}

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		return (List<NotificationSubscription>) query.executeWithMap(paramMap);
	}

	/**
	 * finds the notification history record (if it exists) for this type/entity
	 * combination
	 * 
	 * @param type
	 * @param entityId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public NotificationHistory findNotificationHistory(String type,
			Long entityId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(NotificationHistory.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("type", filterString, paramString, "String", type,
				paramMap);
		appendNonNullParam("entityId", filterString, paramString, "Long",
				entityId, paramMap);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<NotificationHistory> results = (List<NotificationHistory>) query
				.executeWithMap(paramMap);
		if (results != null && results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * saves the notification history object passed in, incrementing the count
	 * and updating the date before saving.
	 * 
	 * @param h
	 * @return
	 */
	public static synchronized NotificationHistory saveNotificationHistory(
			NotificationHistory h) {
		if (h != null) {
			if (h.getCount() == null) {
				h.setCount(1L);
			} else {
				h.setCount(h.getCount() + 1);
			}
			h.setLastNotification(new Date());
			NotificationSubscriptionDao dao = new NotificationSubscriptionDao();
			h = dao.save(h);
		}
		return h;
	}

}