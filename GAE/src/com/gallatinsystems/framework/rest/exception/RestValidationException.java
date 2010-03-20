package com.gallatinsystems.framework.rest.exception;

import com.gallatinsystems.framework.rest.RestError;

/**
 * Validation errors for rest input parameters
 * 
 * @author Christopher Fagiani
 * 
 */
public class RestValidationException extends RestException {

	private static final long serialVersionUID = 9185247960286459263L;

	public RestValidationException(RestError error, String message,
			Exception rootCause) {
		super(error, message, rootCause);
	}
}