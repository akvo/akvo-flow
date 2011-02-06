package com.gallatinsystems.notification.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * used to record details about when notifications last ran
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class NotificationHistory extends BaseDomain {

	private static final long serialVersionUID = -6399811422673038605L;
	private String checksum;
	private Long count;
	private String type;
	private Long entityId;
	private Date lastNotification;

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Date getLastNotification() {
		return lastNotification;
	}

	public void setLastNotification(Date lastNotification) {
		this.lastNotification = lastNotification;
	}

}
