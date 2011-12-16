package com.gallatinsystems.framework.exceptions;

/**
 * exception class that can be used whenever there is a problem with signed data
 * (either mismatched signatures or missing signatures)
 * 
 * @author Christopher Fagiani
 * 
 */
public class SignedDataException extends Exception {

	private static final long serialVersionUID = 8734887355683444379L;

	public SignedDataException(String message) {
		this(message,null);
	}

	public SignedDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
