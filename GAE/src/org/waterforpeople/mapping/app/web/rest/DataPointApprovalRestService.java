/*
 *  Copyright (C) 2016-2017 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    private DataPointApprovalDAO dataPointApprovalDao = new DataPointApprovalDAO();

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

    /**
     * List available DataPointApprovals
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<DataPointApprovalDTO>> listDataPointApprovals(
            @RequestParam(value = "surveyedLocaleId[]", required = false) List<Long> surveyedLocaleIds,
            @RequestParam(value = "surveyedLocaleId", required = false) Long surveyedLocaleId) {
        Map<String, List<DataPointApprovalDTO>> response = new HashMap<String, List<DataPointApprovalDTO>>();

        List<DataPointApproval> approvals = new ArrayList<DataPointApproval>();
        if (surveyedLocaleIds != null) {
            approvals.addAll(dataPointApprovalDao.listBySurveyedLocaleIds(surveyedLocaleIds));
        } else if (surveyedLocaleId != null) {
            approvals.addAll(dataPointApprovalDao.listBySurveyedLocaleId(surveyedLocaleId));
        }

        List<DataPointApprovalDTO> approvalsResponseList = new ArrayList<DataPointApprovalDTO>();
        for (DataPointApproval approval : approvals) {
            approvalsResponseList.add(new DataPointApprovalDTO(approval));
        }

        response.put("data_point_approvals", approvalsResponseList);
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{dataPointApprovalId}")
    @ResponseBody
    public Map<String, Object> updateDataPointApproval(
            @RequestBody DataPointApprovalPayload dataPointApprovalPayload,
            @PathVariable Long dataPointApprovalId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto status = new RestStatusDto();

        DataPointApproval updatedApproval = dataPointApprovalPayload.getData_point_approval()
                .getDataPointApproval();

        final DataPointApproval storedApproval = dataPointApprovalDao.getByKey(dataPointApprovalId);
        if (storedApproval != null) {
            storedApproval.setApprovalDate(new Date());
            storedApproval.setStatus(updatedApproval.getStatus());
            storedApproval.setComment(updatedApproval.getComment());

            response.put("data_point_approval",
                    new DataPointApprovalDTO(dataPointApprovalDao.save(storedApproval)));
        }

        response.put("meta", status);
        return response;
    }
}
