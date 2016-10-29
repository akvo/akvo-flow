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

package com.gallatinsystems.survey.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * A DataPointApproval entity stores information about data approval steps that have been applied to
 * a specific data point
 */
@PersistenceCapable
public class DataPointApproval extends BaseDomain {

    private static final long serialVersionUID = -7346864739901359663L;

    private Long surveyedLocaleId;

    private Long approvalStepId;

    private Long approverUserId;

    private String approverUserName;

    private Date approvalDate;

    private String comment;

    private ApprovalStatus status;

    public enum ApprovalStatus {
        APPROVED, REJECTED, PENDING
    }

    public Long getSurveyedLocaleId() {
        return surveyedLocaleId;
    }

    public void setSurveyedLocaleId(Long surveyedLocaleId) {
        this.surveyedLocaleId = surveyedLocaleId;
    }

    public Long getApprovalStepId() {
        return approvalStepId;
    }

    public void setApprovalStepId(Long approvalStepId) {
        this.approvalStepId = approvalStepId;
    }

    public Long getApproverUserId() {
        return approverUserId;
    }

    public void setApproverUserId(Long approverlUserId) {
        this.approverUserId = approverlUserId;
    }

    public String getApproverUserName() {
        return approverUserName;
    }

    public void setApproverUserName(String approverUserName) {
        this.approverUserName = approverUserName;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

}
