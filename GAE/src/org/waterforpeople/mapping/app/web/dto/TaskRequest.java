/*
 *  Copyright (C) 2010-2015 Stichting Akvo (Akvo Foundation)
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

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request to the task queue servlet
 *
 * @author Christopher Fagiani
 */
public class TaskRequest extends RestRequest {

    private static final long serialVersionUID = 3002548651592779931L;
    public static final String ADD_ACCESS_POINT_ACTION = "addAccessPoint";
    public static final String PROCESS_FILE_ACTION = "processFile";
    public static final String UPDATE_AP_GEO_SUB = "updateAccessPointGeoSub";

    public static final String FILE_NAME_PARAM = "fileName";
    public static final String SURVEY_ID_PARAM = "surveyId";
    public static final String ANDROID_ID = "androidId";
    public static final String PHONE_NUM_PARAM = "phoneNumber";
    public static final String IMEI_PARAM = "imei";
    public static final String CHECKSUM_PARAM = "checksum";
    public static final String OFFSET_PARAM = "offset";
    public static final String ACCESS_POINT_ID_PARAM = "accessPointId";
    public static final String TASK_RETRY_PARAM = "retry";

    private String fileName;
    private Long surveyId;
    private String androidId;
    private String phoneNumber;
    private String imei;
    private String checksum;
    private Integer offset = 0;
    private Long accessPointId = null;
    private Integer retry = 0;

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        fileName = req.getParameter(FILE_NAME_PARAM);
        phoneNumber = req.getParameter(PHONE_NUM_PARAM);
        androidId = req.getParameter(ANDROID_ID);
        imei = req.getParameter(IMEI_PARAM);
        checksum = req.getParameter(CHECKSUM_PARAM);
        if (req.getParameter(ACCESS_POINT_ID_PARAM) != null) {
            setAccessPointId(Long.parseLong(req.getParameter(ACCESS_POINT_ID_PARAM)));
        }
        try {
            if (req.getParameter(SURVEY_ID_PARAM) != null) {
                surveyId = Long.parseLong(req.getParameter(SURVEY_ID_PARAM));
            }
        } catch (Exception e) {
            addError(new RestError(RestError.BAD_DATATYPE_CODE,
                    RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
                            + " must be an integer"));
        }

        if (req.getParameter(OFFSET_PARAM) != null) {
            try {
                offset = Integer.parseInt(req.getParameter(OFFSET_PARAM).trim());
            } catch (Exception e) {
                offset = 0;
            }
        }

        if (req.getParameter(TASK_RETRY_PARAM) != null) {
            try {
                retry = Integer.parseInt(req.getParameter(TASK_RETRY_PARAM).trim());
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, TASK_RETRY_PARAM
                                + " must be an integer"));
            }
        }
    }

    @Override
    public void populateErrors() {
        if (getAction() == null) {
            String errorMsg = ACTION_PARAM + " is mandatory";
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
        }
    }

    public void setAccessPointId(Long accessPointId) {
        this.accessPointId = accessPointId;
    }

    public Long getAccessPointId() {
        return accessPointId;
    }

}
