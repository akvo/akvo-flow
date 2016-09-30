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

import org.waterforpeople.mapping.app.web.dto.DataPointApprovalDTO;

/**
 * Extra payload class with level of indirection, created in order to accommodate the JSON format
 * sent by ember.js.. i.e. { data_point_approval:{name:.., ordered:...} }
 */
public class DataPointApprovalPayload {

    public DataPointApprovalDTO data_point_approval;

    public DataPointApprovalDTO getData_point_approval() {
        return data_point_approval;
    }

    public void setData_point_approval(DataPointApprovalDTO data_point_approval) {
        this.data_point_approval = data_point_approval;
    }
}
