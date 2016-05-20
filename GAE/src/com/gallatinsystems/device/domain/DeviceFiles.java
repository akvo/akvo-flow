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

package com.gallatinsystems.device.domain;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.waterforpeople.mapping.domain.Status;
import org.waterforpeople.mapping.domain.Status.StatusCode;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * persistent class for storing a record of which file(s) were uploaded by which devices and their
 * processing status.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DeviceFiles extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Persistent
    private String URI = null;
    @Persistent
    private Date uploadDateTime = null;
    @Persistent
    private Status.StatusCode processedStatus = null;
    @Persistent
    private Status status = null;
    @Persistent
    private String processDate = null;
    private Text processingMessageText = null;
    private Long surveyInstanceId = null;

    private String androidId;
    private String phoneNumber;
    private String checksum;
    private String imei;

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

    public String getURI() {
        return URI;
    }

    public String getProcessDate() {
        return processDate;
    }

    public void setProcessDate(String processDate) {
        this.processDate = processDate;
    }

    public void setURI(String uri) {
        URI = uri;
    }

    public Date getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Date uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public Status.StatusCode getProcessedStatus() {
        return processedStatus;
    }

    public void setProcessedStatus(StatusCode statusCode) {
        this.processedStatus = statusCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getAndroidId() {
        return androidId;
    }
    
    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeviceFiles: ");
        sb.append("\n   Key: " + key.toString());
        sb.append("\n   URI: " + URI);
        sb.append("\n   ProcessDate: " + this.processDate);
        sb.append("\n   Status: " + status);
        sb.append("\n   ProcessedStatus: " + this.processedStatus);
        return sb.toString();
    }

    public void addProcessingMessage(String message) {
        if (processingMessageText != null)
            processingMessageText = new Text(processingMessageText.getValue()
                    + "\n" + message);
        else
            processingMessageText = new Text(message);
    }

    public void setProcessingMessageText(Text processingMessage) {
        this.processingMessageText = processingMessage;
    }

    public String getProcessingStringMessage() {
        return processingMessageText.getValue();
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public Text getProcessingMessageText() {
        // TODO Auto-generated method stub
        return processingMessageText;
    }
}
