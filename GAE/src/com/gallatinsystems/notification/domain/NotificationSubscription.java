package com.gallatinsystems.notification.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Used to record who has opted-in to notifications and how to notify them
 * 
 * 
 * @author Christopher Fagiani
 */

@PersistenceCapable
public class NotificationSubscription extends BaseDomain {

	private static final long serialVersionUID = 5531603859192908616L;
	private String notificationType;
	private String notificationMethod;
	private String notificationDestination;
	private String notificationOption;
	private Long entityId;
	private Date expiryDate;

	public String getNotificationOption() {
		return notificationOption;
	}

	public void setNotificationOption(String notificationOption) {
		this.notificationOption = notificationOption;
	}

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