package org.waterforpeople.mapping.notification;

import java.util.TreeMap;

import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.notification.NotificationHandler;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationHistory;

/**
 * base for functionality common across notification handlers
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class BaseNotificationHandler implements NotificationHandler {
	private final static String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
	protected static String FROM_ADDRESS;
	protected NotificationSubscriptionDao dao;

	protected BaseNotificationHandler() {
		FROM_ADDRESS = PropertyUtil.getProperty(EMAIL_FROM_ADDRESS_KEY);
		dao = new NotificationSubscriptionDao();
	}

	protected NotificationHistory getHistory(String type, Long id) {

		NotificationHistory hist = dao.findNotificationHistory(type, id);
		if (hist == null) {
			hist = new NotificationHistory();
			hist.setType(type);
			hist.setEntityId(id);
		}
		return hist;
	}

	/**
	 * sends a mail
	 * 
	 * @param recipients
	 * @param subject
	 * @param body
	 * @return
	 */
	protected Boolean sendMail(TreeMap<String, String> recipients,
			String subject, String body) {
		return MailUtil.sendMail(FROM_ADDRESS, "FLOW", recipients, subject,
				body);
	}

}
