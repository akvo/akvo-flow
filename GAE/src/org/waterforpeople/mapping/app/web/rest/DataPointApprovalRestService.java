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

package org.waterforpeople.mapping.app.web.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.CurrentUserServlet;
import org.waterforpeople.mapping.app.web.dto.DataPointApprovalDTO;
import org.waterforpeople.mapping.app.web.rest.dto.DataPointApprovalPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.survey.dao.DataPointApprovalDAO;
import com.gallatinsystems.survey.domain.DataPointApproval;
import com.gallatinsystems.user.domain.User;

@Controller
@RequestMapping("/data_point_approvals")
public class DataPointApprovalRestService {

    @Inject
    private DataPointApprovalDAO dataPointApprovalDao;

    /**
     * Create a new DataPointApproval
     *
     * @param requestPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> createDataPointApproval(
            @RequestBody DataPointApprovalPayload dataPointApprovalPayload) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto status = new RestStatusDto();

        DataPointApproval approval = dataPointApprovalPayload.getData_point_approval()
                .getDataPointApproval();

        approval.setApprovalDate(new Date());

        User currentUser = CurrentUserServlet.getCurrentUser();
        approval.setApproverUserId(currentUser.getKey().getId());
        approval.setApproverUserName(currentUser.getEmailUserName());

        response.put("data_point_approval",
                new DataPointApprovalDTO(dataPointApprovalDao.save(approval)));
        response.put("meta", status);
        return response;
    }
}
