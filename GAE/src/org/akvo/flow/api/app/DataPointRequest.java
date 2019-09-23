/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.api.app;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * data structure for transferring data points
 */
public class DataPointRequest extends RestRequest {
    private static final long serialVersionUID = 1L;
    private static final String SURVEY_ID_PARAM = "surveyId";
    private static final String IMEI_PARAM = "imei";
    private static final String PHONE_NUMBER_PARAM = "phoneNumber";
    private static final String ANDROID_ID_PARAM = "androidId";
    private static final String DEVICE_ID_PARAM = "deviceId";
    private static final String LAST_UPDATE_TIME_PARAM = "lastUpdateTime";
    private static final String CURSOR_PARAM = "cursor";

    private Long surveyId;
    private String imei;
    private String phoneNumber;
    private String androidId;
    private String deviceId;
    @Deprecated
    private Date lastUpdateTime;
    @Deprecated
    private String cursor;

    @Override
    protected void populateErrors() {
        // TODO do a better job in error population
        if (surveyId == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE, "parameter wrong",
                    "missing surveyId"));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM));
        imei = req.getParameter(IMEI_PARAM);
        phoneNumber = req.getParameter(PHONE_NUMBER_PARAM);
        androidId = req.getParameter(ANDROID_ID_PARAM);
        deviceId = req.getParameter(DEVICE_ID_PARAM);
        cursor = req.getParameter(CURSOR_PARAM);
        if (req.getParameter(LAST_UPDATE_TIME_PARAM) != null) {
            try {
                Long ts = Long.parseLong(req.getParameter(LAST_UPDATE_TIME_PARAM));
                setLastUpdateTime(new Date(ts));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, LAST_UPDATE_TIME_PARAM
                                + " not a valid timestamp"));
            }
        }
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String getCursor() {
        return cursor;
    }

    @Override
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
