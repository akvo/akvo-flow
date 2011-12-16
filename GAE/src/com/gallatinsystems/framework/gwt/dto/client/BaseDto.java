package com.gallatinsystems.framework.gwt.dto.client;

import java.io.Serializable;

/**
 * base transfer object that should be used when returning data to clients
 * 
 * @author Christopher Fagaini
 * 
 */
public class BaseDto implements Serializable {

	private static final long serialVersionUID = -5905705837362187943L;

	private Long keyId = null;

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public Long getKeyId() {
		return keyId;
	}

}
