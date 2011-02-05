package com.gallatinsystems.notification.dao;

import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
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

		return listByProperty("expiryDate", new Date(), "Date", LTE_OP);
	}

}