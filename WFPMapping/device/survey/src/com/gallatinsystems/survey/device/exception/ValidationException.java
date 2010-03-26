package com.gallatinsystems.survey.device.exception;

/**
 * Exception to be thrown if data provided by the user is invalid
 * 
 * @author Christopher Fagiani
 * 
 */
public class ValidationException extends Exception {

	public static final String TOO_SMALL = "too small";
	public static final String TOO_LARGE = "too large";
	public static final String INVALID_DATATYPE = "bad datatype";

	private static final long serialVersionUID = 677744340304381823L;

	private String type;

	public ValidationException(String message, String type, Exception e) {
		super(message, e);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
