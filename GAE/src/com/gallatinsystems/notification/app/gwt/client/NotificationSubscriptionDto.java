package com.gallatinsystems.notification.app.gwt.client;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * DTO for transferring notificationSubscriptions between client and server
 * 
 * @author Christopher Fagiani
 * 
 */
public class NotificationSubscriptionDto extends BaseDto {

	private static final long serialVersionUID = -1283796572763884626L;
	private String notificationType;
	private String notificationMethod;
	private String notificationDestination;
	private Long entityId;
	private Date expiryDate;

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationMethod() {
		return notificationMethod;
	}

	public void setNotificationMethod(String notificationMethod) {
		this.notificationMethod = notificationMethod;
	}

	public String getNotificationDestination() {
		return notificationDestination;
	}

	public void setNotificationDestination(String notificationDestination) {
		this.notificationDestination = notificationDestination;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

}
