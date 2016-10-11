/*
 *  Copyright (C) 2016 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest.dto;

import org.waterforpeople.mapping.app.web.dto.ApprovalGroupDTO;

/**
 * Extra payload class with level of indirection, created in order to accommodate the JSON format
 * sent by ember.js.. i.e. { approval_group:{name:.., ordered:...} }
 */
public class ApprovalGroupPayload {

    public ApprovalGroupDTO approval_group;

    public ApprovalGroupDTO getApproval_group() {
        return approval_group;
    }

    public void setApproval_group(ApprovalGroupDTO approval_group) {
        this.approval_group = approval_group;
    }
}
