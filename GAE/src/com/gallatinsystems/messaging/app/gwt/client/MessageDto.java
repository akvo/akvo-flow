package com.gallatinsystems.messaging.app.gwt.client;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object for Messages. 
 * 
 * @author Christopher Fagiani
 * 
 */
public class MessageDto extends BaseDto {
	
	private static final long serialVersionUID = -7560107313339712237L;
	private String actionAbout;
	private Long objectId;
	private String message;
	private String objectTitle;
	private String shortMessage;
	private String transactionUUID;

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
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public String getTransactionUUID() {
		return transactionUUID;
	}

	public void setTransactionUUID(String transactionUUID) {
		this.transactionUUID = transactionUUID;
	}

}
