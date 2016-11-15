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

package com.gallatinsystems.diagnostics.app.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * data structure to encapsulate requests to the remote exception servlet
 * 
 * @author Christopher Fagiani
 */
public class RemoteExceptionRequest extends RestRequest {

    private static final long serialVersionUID = 8303938931927567747L;
    private static final String FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<DateFormat> DATE_FMT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(FORMAT_STRING, Locale.US); // Always specify a locale for M2M formatting
        };
    };

    public static final String SAVE_TRACE_ACTION = "saveTrace";

    public static final String PHONE_PARAM = "phoneNumber";
    public static final String DEV_ID_PARAM = "deviceIdentifier";
    public static final String VERSION_PARAM = "version";
    public static final String DATE_PARAM = "date";
    public static final String TRACE_PARAM = "trace";
    public static final String ANDROID_ID_PARAM = "androidId";

    private String phoneNumber;
    private String deviceIdent;
    private String version;
    private Date date;
    private String stackTrace;
    private String androidId;

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceIdent() {
        return deviceIdent;
    }

    public void setDeviceIdent(String deviceIdent) {
        this.deviceIdent = deviceIdent;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

	@Override
    protected void populateErrors() {
        if (date == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, DATE_PARAM
                            + " is required"));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        phoneNumber = req.getParameter(PHONE_PARAM);
        deviceIdent = req.getParameter(DEV_ID_PARAM);
        version = req.getParameter(VERSION_PARAM);
        stackTrace = req.getParameter(TRACE_PARAM);
        androidId = req.getParameter(ANDROID_ID_PARAM);
        if (req.getParameter(DATE_PARAM) != null
                && req.getParameter(DATE_PARAM).trim().length() > 0) {
            try {
                date = DATE_FMT.get().parse(req.getParameter(DATE_PARAM));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, DATE_PARAM
                                + " must be in format: " + FORMAT_STRING));
            }
        }

    }
}
