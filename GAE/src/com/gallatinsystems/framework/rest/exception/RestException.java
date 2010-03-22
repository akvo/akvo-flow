package com.gallatinsystems.framework.rest.exception;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.rest.RestError;

/**
 * generic exception that can be thrown by Rest APis
 * 
 * @author Christopher Fagiani
 * 
 */
public class RestException extends Exception {

	private static final long serialVersionUID = -1700235976011831126L;
	private List<RestError> errors;

	public RestException(List<RestError> errors, String message,
			Exception rootCause) {
		super(message, rootCause);
		this.errors = errors;
	}

	public RestException(RestError err, String message, Exception rootCause) {
		super(message, rootCause);
		errors = new ArrayList<RestError>();
		errors.add(err);
	}

	public List<RestError> getErrors() {
		return errors;
	}
}