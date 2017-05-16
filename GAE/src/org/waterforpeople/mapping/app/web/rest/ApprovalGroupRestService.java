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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.dto.ApprovalGroupDTO;
import org.waterforpeople.mapping.app.web.rest.dto.ApprovalGroupPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.ApprovalGroupDAO;
import com.gallatinsystems.survey.domain.ApprovalGroup;

@Controller
@RequestMapping("/approval_groups")
public class ApprovalGroupRestService {

    private ApprovalGroupDAO approvalGroupDao = new ApprovalGroupDAO();

    /**
     * Create a new ApprovalGroup from posted payload.
     *
     * @param requestPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> createApprovalGroup(
            @RequestBody ApprovalGroupPayload approvalGroupPayload) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto status = new RestStatusDto();

        ApprovalGroup group = approvalGroupPayload.getApproval_group().getApprovalGroup();

        if (group.getName() == null || group.getName().trim().isEmpty()) {
            return null;
        }

        response.put("approval_group", new ApprovalGroupDTO(approvalGroupDao.save(group)));
        response.put("meta", status);
        return response;
    }

    /**
     * Update an existing approval group
     *
     * @param approvalGroupPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{approvalGroupId}")
    @ResponseBody
    public Map<String, Object> updateApprovalGroup(
            @RequestBody ApprovalGroupPayload approvalGroupPayload,
            @PathVariable Long approvalGroupId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto status = new RestStatusDto();

        final ApprovalGroup updatedGroup = approvalGroupPayload.getApproval_group()
                .getApprovalGroup();

        if (updatedGroup.getName() == null || updatedGroup.getName().trim().isEmpty()) {
            return null;
        }

        final ApprovalGroup storedGroup = approvalGroupDao.getByKey(approvalGroupId);
        if (storedGroup != null) {
            updatedGroup.setKey(storedGroup.getKey());
            response.put("approval_group",
                    new ApprovalGroupDTO(approvalGroupDao.save(updatedGroup)));
        }
        response.put("meta", status);

        return response;
    }

    /**
     * Delete existing approval groups
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{approvalGroupId}")
    @ResponseBody
    public void deleteApprovalGroup(@PathVariable Long approvalGroupId) {
        final ApprovalGroup approvalGroup = approvalGroupDao.getByKey(approvalGroupId);
        if (approvalGroup != null) {
            approvalGroupDao.delete(approvalGroup);
        }
    }

    /**
     * List all the available ApprovalGroups
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<ApprovalGroupDTO>> listApprovalGroups() {
        Map<String, List<ApprovalGroupDTO>> response = new HashMap<String, List<ApprovalGroupDTO>>();

        List<ApprovalGroupDTO> approvalGroupsResponseList = new ArrayList<ApprovalGroupDTO>();
        for (ApprovalGroup group : approvalGroupDao.list(Constants.ALL_RESULTS)) {
            approvalGroupsResponseList.add(new ApprovalGroupDTO(group));
        }

        response.put("approval_groups", approvalGroupsResponseList);
        return response;
    }

    /**
     * List a specific ApprovalGroup
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{approvalGroupId}")
    @ResponseBody
    public Map<String, ApprovalGroupDTO> findApprovalGroup(@PathVariable Long approvalGroupId) {
        Map<String, ApprovalGroupDTO> response = new HashMap<String, ApprovalGroupDTO>();

        ApprovalGroup approvalGroup = approvalGroupDao.getByKey(approvalGroupId);
        if (approvalGroup != null) {
            response.put("approval_group", new ApprovalGroupDTO(approvalGroup));
        }
        return response;
    }
}
