package com.gallatinsystems.framework.exceptions;

/**
 * 
 * Exception to be thrown if something attempts to perform an illegal deletion.
 * An example of an illegal deletion is if a user tries to delete a survey
 * Question that already has responses in the system.
 * 
 * @author Christopher Fagiani
 */
public class IllegalDeletionException extends Exception {

	private static final long serialVersionUID = -8158600040817892556L;
	private String error;

	public IllegalDeletionException() {
		this("unknown", null);
	}

	public IllegalDeletionException(String err) {
		this(err, null);
	}

	public IllegalDeletionException(String msg, Exception root) {
		super(msg, root);
		error = msg;
	}

	public String getError() {
		return error;
	}
}
