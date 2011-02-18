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

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			if (o instanceof NotificationSubscriptionDto) {
				NotificationSubscriptionDto otherDto = (NotificationSubscriptionDto) o;
				if (notificationType != null && entityId != null) {
					if (notificationType.equals(otherDto.getNotificationType())
							&& entityId.equals(otherDto.getEntityId())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean validate() {
		if (entityId == null || notificationType == null || expiryDate == null
				|| notificationDestination == null) {
			return false;
		} else {
			return true;
		}
	}
}
