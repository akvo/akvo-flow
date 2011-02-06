package com.gallatinsystems.notification;

/**
 * class capable of sending a notification
 * 
 * @author Christopher Fagiani
 * 
 */
public interface NotificationHandler {
	
	public void generateNotification(Long entityId, String destinations, String serverBase);

}
