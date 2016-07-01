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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.dto.ApprovalGroupDTO;

import com.gallatinsystems.survey.dao.ApprovalGroupDAO;
import com.gallatinsystems.survey.domain.ApprovalGroup;

@Controller
@RequestMapping("/approval_group")
public class ApprovalGroupRestService {

    @Inject
    private ApprovalGroupDAO approvalGroupDao;

    /**
     * Create a new ApprovalGroup from posted payload.
     *
     * @param requestPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, ApprovalGroupDTO> createApprovalGroup(
            @RequestBody ApprovalGroupDTO approvalGroupPayload) {
        final Map<String, ApprovalGroupDTO> response = new HashMap<String, ApprovalGroupDTO>();
        ApprovalGroup group = approvalGroupPayload.getApprovalGroup();

        if (group.getName() == null || group.getName().trim().isEmpty()) {
            return null;
        }

        response.put("approval_group", new ApprovalGroupDTO(approvalGroupDao.save(group)));
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
    public Map<String, ApprovalGroupDTO> updateApprovalGroup(
            @RequestBody ApprovalGroupDTO approvalGroupPayload, @PathVariable Long approvalGroupId) {
        final Map<String, ApprovalGroupDTO> response = new HashMap<String, ApprovalGroupDTO>();
        final ApprovalGroup updatedGroup = approvalGroupPayload.getApprovalGroup();

        if (updatedGroup.getName() == null || updatedGroup.getName().trim().isEmpty()) {
            return null;
        }

        final ApprovalGroup storedGroup = approvalGroupDao.getByKey(approvalGroupId);
        updatedGroup.setKey(storedGroup.getKey());

        response.put("approval_group", new ApprovalGroupDTO(approvalGroupDao.save(updatedGroup)));
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
}
