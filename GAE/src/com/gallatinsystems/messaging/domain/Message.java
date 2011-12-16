package com.gallatinsystems.messaging.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * persistent object for storing system messages that are to be displayed to the
 * user.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class Message extends BaseDomain {

	private static final long serialVersionUID = -7829182245275723095L;

	private String actionAbout;
	private Long objectId;
	private Text message;
	private String objectTitle;
	private String shortMessage;
	private String transactionUUID;
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
	}

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
		if (message != null) {
			return message.getValue();
		} else {
			return null;
		}
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

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

}
