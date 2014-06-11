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

package com.gallatinsystems.framework.rest;

import java.io.Serializable;

/**
 * base class for any rest errors that will be returned by the rest API to the client
 * 
 * @author Christopher Fagiani
 */
public class RestError implements Serializable {

    private static final long serialVersionUID = 6117815141755911193L;

    public static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";
    public static final String UNNOWN_ERROR_CODE = "5000";
    public static final String MISSING_PARAM_ERROR_CODE = "5001";
    public static final String MISSING_PARAM_ERROR_MESSAGE = "Missing mandatory parameter";
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
