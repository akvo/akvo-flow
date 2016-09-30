/*
 *  Copyright (C) 2016 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo Flow.
 *
 *  Akvo Flow is free software: you can redistribute it and modify it under the terms of
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

import org.codehaus.jackson.annotate.JsonIgnore;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.domain.DataPointApproval;
import com.gallatinsystems.survey.domain.DataPointApproval.ApprovalStatus;

/**
 * DataPointApprovalDTO - used for only exposing specific data point approval properties to the UI
 * side
 */
@SuppressWarnings("serial")
public class DataPointApprovalDTO extends BaseDto {

    private DataPointApproval dataPointApproval;

    public DataPointApprovalDTO() {
        dataPointApproval = new DataPointApproval();
    }

    public DataPointApprovalDTO(DataPointApproval approval) {
        this.dataPointApproval = approval;
        if (approval.getKey() != null) {
            this.setKeyId(dataPointApproval.getKey().getId());
        }
    }

    public Long getSurveyedLocaleId() {
        return dataPointApproval.getSurveyedLocaleId();
    }

    public void setSurveyedLocaleId(Long surveyedLocaleId) {
        dataPointApproval.setSurveyedLocaleId(surveyedLocaleId);
    }

    public Long getApprovalStepId() {
        return dataPointApproval.getApprovalStepId();
    }

    public void setApprovalStepId(Long approvalStepId) {
        dataPointApproval.setApprovalStepId(approvalStepId);
    }

    public String getApproverUserName() {
        return dataPointApproval.getApproverUserName();
    }

    public void setApproverUserName(String approverUserName) {
        dataPointApproval.setApproverUserName(approverUserName);
    }

    public String getComment() {
        return dataPointApproval.getComment();
    }

    public void setComment(String comment) {
        dataPointApproval.setComment(comment);
    }

    public ApprovalStatus getStatus() {
        return dataPointApproval.getStatus();
    }

    public void setStatus(ApprovalStatus status) {
        dataPointApproval.setStatus(status);
    }

    @JsonIgnore
    public DataPointApproval getDataPointApproval() {
        return dataPointApproval;
    }
}
