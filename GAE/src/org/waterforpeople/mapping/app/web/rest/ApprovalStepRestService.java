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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.dto.ApprovalStepDTO;
import org.waterforpeople.mapping.app.web.rest.dto.ApprovalStepPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.ApprovalStepDAO;
import com.gallatinsystems.survey.domain.ApprovalStep;

@Controller
@RequestMapping("/approval_steps")
public class ApprovalStepRestService {

    private ApprovalStepDAO approvalStepDao = new ApprovalStepDAO();

    /**
     * Create a new ApprovalStep from posted payload.
     *
     * @param requestPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> createApprovalStep(
            @RequestBody ApprovalStepPayload approvalStepPayload) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final ApprovalStep step = approvalStepPayload.getApproval_step().getApprovalStep();
        final RestStatusDto status = new RestStatusDto();

        if (step.getApprovalGroupId() == 0L || step.getTitle() == null
                || step.getTitle().trim().isEmpty()) {
            return null;
        }

        response.put("approval_step", new ApprovalStepDTO(approvalStepDao.save(step)));
        response.put("meta", status);
        return response;
    }

    /**
     * Update an existing approval step
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{approvalStepId}")
    @ResponseBody
    public Map<String, Object> updateApprovalStep(
            @RequestBody ApprovalStepPayload approvalStepPayload, @PathVariable Long approvalStepId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto status = new RestStatusDto();
        final ApprovalStep updatedStep = approvalStepPayload.getApproval_step().getApprovalStep();

        if (updatedStep.getApprovalGroupId() == 0L || updatedStep.getTitle() == null
                || updatedStep.getTitle().trim().isEmpty()) {
            return null;
        }

        final ApprovalStep storedStep = approvalStepDao.getByKey(approvalStepId);
        if (storedStep != null) {
            updatedStep.setKey(storedStep.getKey());
            response.put("approval_step",
                    new ApprovalStepDTO(approvalStepDao.save(updatedStep)));
        }
        response.put("meta", status);

        return response;
    }

    /**
     * Delete existing approval step
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{approvalStepId}")
    @ResponseBody
    public void deleteApprovalStep(@PathVariable Long approvalStepId) {
        final ApprovalStep approvalStep = approvalStepDao.getByKey(approvalStepId);
        if (approvalStep != null) {
            approvalStepDao.delete(approvalStep);
        }
    }

    /**
     * List a specific ApprovalStep
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{approvalStepId}")
    @ResponseBody
    public Map<String, ApprovalStepDTO> findApprovalStep(@PathVariable Long approvalStepId) {
        Map<String, ApprovalStepDTO> response = new HashMap<String, ApprovalStepDTO>();

        ApprovalStep approvalStep = approvalStepDao.getByKey(approvalStepId);
        if (approvalStep != null) {
            response.put("approval_step", new ApprovalStepDTO(approvalStep));
        }
        return response;
    }

    /**
     * List all the approval steps or filter them by a specific approval group
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<ApprovalStepDTO>> listApprovalSteps(
            @RequestParam(value = "approvalGroupId", required = false) Long approvalGroupId) {
        Map<String, List<ApprovalStepDTO>> response = new HashMap<String, List<ApprovalStepDTO>>();
        List<ApprovalStep> stepsList = new ArrayList<ApprovalStep>();
        if (approvalGroupId != null) {
            stepsList.addAll(approvalStepDao.listByApprovalGroup(approvalGroupId));
        } else {
            stepsList.addAll(approvalStepDao.list(Constants.ALL_RESULTS));
        }

        List<ApprovalStepDTO> stepsDtoList = new ArrayList<ApprovalStepDTO>();
        for (ApprovalStep step : stepsList) {
            stepsDtoList.add(new ApprovalStepDTO(step));
        }
        response.put("approval_steps", stepsDtoList);
        return response;
    }
}
