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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

/**
 * encapsulates requests to the SurveyManager apis
 * 
 * @author Christopher Fagiani
 */
public class SurveyManagerRequest extends RestRequest {

    private static final long serialVersionUID = -1914332708852551948L;

    public static final String GET_AVAIL_DEVICE_SURVEY_ACTION = "getAvailableSurveysDevice";
    public static final String GET_SURVEY_HEADER_ACTION = "getSurveyHeader";
    public static final String GET_ZIP_FILE_URL_ACTION = "getZipFileUrls";

    private static final String SURVEY_INSTANCE_PARAM = "surveyInstanceId";
    private static final String SURVEY_ID_PARAM = "surveyId";
    private static final String VERSION_PARAM = "ver";
    private static final String SURVEY_DOC_PARAM = "surveyDocument";
    private static final String IMEI_PARAM = "imei";
    private static final String PHONE_NUM_PARAM = "devicePhoneNumber";
    @Deprecated
    private static final String PHONE_NUM_ALT_PARAM = "phoneNumber";
    private static final String FILE_START_DATE_PARAM = "startDate";
    private static final String DEVICE_ID_PARAM = "devId";

    public static final String GET_AVAIL_DEVICE_SURVEYGROUP_ACTION = "getAvailableSurveyGroupsDevice";

    private Long surveyId;
    private Long surveyInstanceId;
    private String surveyDoc;
    private String phoneNumber;
    private String imei;
    private Date startDate;
    private String deviceId;
    private String version;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public String getSurveyDoc() {
        return surveyDoc;
    }

    public void setSurveyDoc(String surveyDoc) {
        this.surveyDoc = surveyDoc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStartDate(String dateString) throws Exception {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
            Date convertedDate = dateFormat.parse(dateString);
            setStartDate(convertedDate);
        } catch (Exception ex) {
            throw new Exception("Could not parse date param");
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        surveyInstanceId = parseLong(req.getParameter(SURVEY_INSTANCE_PARAM),
                SURVEY_INSTANCE_PARAM);
        phoneNumber = req.getParameter(PHONE_NUM_PARAM);
        if (phoneNumber == null || phoneNumber.trim().length() == 0) {
            phoneNumber = req.getParameter(PHONE_NUM_ALT_PARAM);
        }
        imei = req.getParameter(IMEI_PARAM);
        version = req.getParameter(VERSION_PARAM);
        deviceId = req.getParameter(DEVICE_ID_PARAM).trim();
        surveyId = parseLong(req.getParameter(SURVEY_ID_PARAM), SURVEY_ID_PARAM);
        surveyDoc = req.getParameter(SURVEY_DOC_PARAM);
        if (req.getParameter(FILE_START_DATE_PARAM) != null)
            setStartDate(req.getParameter(FILE_START_DATE_PARAM));
    }

    @Override
    public void populateErrors() {
        // no-op right now?
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
