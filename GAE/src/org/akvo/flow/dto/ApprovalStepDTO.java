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

package org.akvo.flow.dto;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.domain.ApprovalStep;

/**
 * ApprovalStepDTO - used for only exposing specific properties to the UI side
 */

@SuppressWarnings("serial")
public class ApprovalStepDTO extends BaseDto {

    private ApprovalStep approvalStep;

    public ApprovalStepDTO() {
        approvalStep = new ApprovalStep();
    }

    public ApprovalStepDTO(ApprovalStep step) {
        this.approvalStep = step;
    }

    public long getApprovalGroupId() {
        return approvalStep.getApprovalGroupId();
    }

    public void setApprovalGroupId(long approvalGroupId) {
        approvalStep.setApprovalGroupId(approvalGroupId);
    }

    public int getOrder() {
        return approvalStep.getOrder();
    }

    public void setOrder(int order) {
        approvalStep.setOrder(order);
    }

    public String getTitle() {
        return approvalStep.getTitle();
    }

    public void setTitle(String title) {
        approvalStep.setTitle(title);
    }

}
