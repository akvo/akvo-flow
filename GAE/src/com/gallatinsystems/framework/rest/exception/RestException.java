package com.gallatinsystems.framework.rest.exception;

import com.gallatinsystems.framework.rest.RestError;

/**
 * generic exception that can be thrown by Rest APis
 * @author Christopher Fagiani
 *
 */
public class RestException extends Exception {
	
	private static final long serialVersionUID = -1700235976011831126L;
	private RestError error;

	public RestException(RestError error, String message, Exception rootCause) {
		super(message, rootCause);
		this.error = error;
	}

	public RestError getError() {
		return error;
	}
}