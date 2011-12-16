package com.gallatinsystems.notification;

/**
 * class capable of sending a notification
 * 
 * @author Christopher Fagiani
 * 
 */
public interface NotificationHandler {

	/**
	 * generates the notification content and delievers it to the recipients
	 * based on the destOptions.
	 * 
	 * @param type
	 * @param entityId
	 * @param destinations
	 * @param destOptions
	 * @param serverBase
	 */
	public void generateNotification(String type, Long entityId,
			String destinations, String destOptions, String serverBase);

}
