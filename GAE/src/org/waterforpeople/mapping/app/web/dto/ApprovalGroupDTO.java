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

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.domain.ApprovalGroup;

/**
 * ApprovalGroupDTO - used for only exposing specific properties to the UI side
 */
@SuppressWarnings("serial")
public class ApprovalGroupDTO extends BaseDto {

    private ApprovalGroup approvalGroup;

    public ApprovalGroupDTO() {
        approvalGroup = new ApprovalGroup();
    }

    public ApprovalGroupDTO(ApprovalGroup group) {
        this.approvalGroup = group;
    }

    public String getName() {
        return approvalGroup.getName();
    }

    public void setName(String name) {
        approvalGroup.setName(name);
    }

    public Boolean getOrdered() {
        return approvalGroup.getOrdered();
    }

    public void setOrdered(Boolean ordered) {
        approvalGroup.setOrdered(ordered);
    }
}
