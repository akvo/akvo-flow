package com.gallatinsystems.framework.rest;

/**
 * base class for any rest errors that will be returned by the rest API to the
 * client
 * 
 * @author Christopher Fagiani
 * 
 */
public class RestError {

	public static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";
	public static final String UNNOWN_ERROR_CODE = "5000";

	private String errorCode;
	private String errorMessage;

	public RestError() {
		errorCode = UNNOWN_ERROR_CODE;
		errorMessage = UNKNOWN_ERROR_MESSAGE;
	}

	public RestError(String code, String msg) {
		errorCode = code;
		errorMessage = msg;
	}
}