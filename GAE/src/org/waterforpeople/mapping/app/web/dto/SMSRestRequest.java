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

package org.waterforpeople.mapping.app.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * represents the data sent over by the an SMS gateway
 * 
 * @author Christopher Fagiani
 */
public class SMSRestRequest extends RestRequest {

    private static final long serialVersionUID = -4090095229806070007L;
    private static final String API_ID_PARAM = "Api_id";
    private static final String FROM_PARAM = "from";
    private static final String TO_PARAM = "to";
    private static final String TIME_PARAM = "timestamp";
    private static final String CHARSET_PARAM = "charset";
    private static final String TEXT_PARAM = "text";

    private static final ThreadLocal<DateFormat> DATE_FMT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        };
    };

    private String apiId;
    private String from;
    private String to;
    private Date timestamp;
    private String text;
    private String charset;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void populateErrors() {
        if (text == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, TEXT_PARAM));
        }

    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        text = req.getParameter(TEXT_PARAM);
        if (text != null) {
            text = text.trim();
        }
        from = req.getParameter(FROM_PARAM);
        to = req.getParameter(TO_PARAM);
        String dateString = req.getParameter(TIME_PARAM);
        if (dateString != null) {
            try {
                timestamp = DATE_FMT.get().parse(dateString);
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE,
                        "Date format: yyyy-MM-dd HH:mm:ss"));
            }
        }
        charset = req.getParameter(CHARSET_PARAM);
        apiId = req.getParameter(API_ID_PARAM);
    }

}
