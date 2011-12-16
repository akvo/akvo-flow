package com.gallatinsystems.framework.gwt.dto.client;

import java.io.Serializable;

/**
 * base class to be used as a response from a service call. This wrapper object
 * supports a payload (parameterized type) as well as an optional cursor to
 * facilitate building services that support pagination.
 * 
 * @author Christopher Fagiani
 * 
 * @param <T>
 *            - payload type
 */
public class ResponseDto<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -670907947130363885L;
	public static final int DEFAULT_PAGE_SIZE = 20;
	private String cursorString = null;
	private T payload = null;

	public ResponseDto() {
	}

	/**
	 * returns the current cursor that corresponds to the payload. Returns null
	 * if no cursor is set.
	 * 
	 * @return
	 */
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
