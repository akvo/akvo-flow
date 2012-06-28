/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
