package com.gallatinsystems.notification;

/**
 * class capable of sending a notification
 * 
 * @author Christopher Fagiani
 * 
 */
public interface NotificationHandler {
	
	public void generateNotification(String type, Long entityId, String destinations,String destOptions, String serverBase);

}
