package com.gallatinsystems.messaging.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;
@PersistenceCapable
public class Message extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7829182245275723095L;

	private String actionAbout = null;
	private Long objectId = null; 
	private Text message = null;
	private String transactionUUID = null;
	
	public String getActionAbout() {
		return actionAbout;
	}
	public void setActionAbout(String actionAbout) {
		this.actionAbout = actionAbout;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public String getMessage() {
		return message.getValue();
	}
	public void setMessage(String message) {
		this.message = new Text(message);
	}
	public void setTransactionUUID(String transactionUUID) {
		this.transactionUUID = transactionUUID;
	}
	public String getTransactionUUID() {
		return transactionUUID;
	}	
}
