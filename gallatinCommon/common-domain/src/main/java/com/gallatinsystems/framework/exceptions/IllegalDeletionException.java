package com.gallatinsystems.framework.exceptions;

public class IllegalDeletionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8158600040817892556L;
	private String error;

	public IllegalDeletionException() {
		super();
		error = "unknown";
	}

	public IllegalDeletionException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
}
