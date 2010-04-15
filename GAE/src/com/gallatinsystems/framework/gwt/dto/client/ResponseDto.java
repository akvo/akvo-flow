package com.gallatinsystems.framework.gwt.dto.client;

import java.io.Serializable;
import java.util.List;

public class ResponseDto<T extends Serializable> implements Serializable {
	/** * */
	private static final long serialVersionUID = -670907947130363885L;
	private String cursorString = null;
	private T payload = null;

	public ResponseDto() {
	}

	public String getCursorString() {
		return cursorString;
	}

	public void setCursorString(String cursorString) {
		this.cursorString = cursorString;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

}
