package org.waterforpeople.mapping.app.gwt.client.framework;

import java.io.Serializable;

public class BaseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5905705837362187943L;
	
	private Long keyId = null;

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public Long getKeyId() {
		return keyId;
	}

}
