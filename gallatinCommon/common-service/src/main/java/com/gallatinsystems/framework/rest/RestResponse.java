package com.gallatinsystems.framework.rest;

import java.io.Serializable;

/**
 * base class to unify rest responses.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RestResponse implements Serializable{

	private static final long serialVersionUID = -4783160185098390267L;
	private int resultCount;
	private String message;
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	private int offset;

}