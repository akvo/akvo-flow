package com.gallatinsystems.framework.rest;

import java.io.Serializable;

/**
 * base class for any rest errors that will be returned by the rest API to the
 * client
 * 
 * @author Christopher Fagiani
 * 
 */
public class RestError implements Serializable{

	private static final long serialVersionUID = 6117815141755911193L;
	
	public static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";
	public static final String UNNOWN_ERROR_CODE = "5000";
	public static final String MISSING_PARAM_ERROR_CODE = "5001";
	public static final String MISSING_PRAM_ERROR_MESSAGE = "Missing mandatory parameter";
	public static final String BAD_DATATYPE_CODE = "5002";
	public static final String BAD_DATATYPE_MESSAGE = "Invalid data type";

	private String errorCode;
	private String errorMessage;
	private String text;

	public RestError() {
		errorCode = UNNOWN_ERROR_CODE;
		errorMessage = UNKNOWN_ERROR_MESSAGE;
		text = "";
	}

	public RestError(String code, String msg, String text) {
		errorCode = code;
		errorMessage = msg;
		this.text = text;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		return errorCode + " - " + errorMessage + ": " + text;
	}

}