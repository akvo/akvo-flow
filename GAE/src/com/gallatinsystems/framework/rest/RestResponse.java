package com.gallatinsystems.framework.rest;

/**
 * base class to unify rest responses.
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class RestResponse {

	private int resultCount;

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