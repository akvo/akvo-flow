/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.web.dto.ApprovalStepDTO;
import org.waterforpeople.mapping.app.web.dto.DataApprovalRequest;
import org.waterforpeople.mapping.app.web.dto.DataApprovalRestResponse;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.ApprovalStepDAO;
import com.gallatinsystems.survey.domain.ApprovalStep;

import static org.waterforpeople.mapping.app.web.dto.DataApprovalRequest.*;

public class DataApprovalServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = 1706847643104220714L;
    private static final Logger log = Logger.getLogger(DataApprovalServlet.class.getSimpleName());
    private ApprovalStepDAO approvalstepsDAO;

    public DataApprovalServlet() {
        setMode(JSON_MODE);
        approvalstepsDAO = new ApprovalStepDAO();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = super.getRequest();
        RestRequest restRequest = new DataApprovalRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DataApprovalRequest approvalServletRequest = (DataApprovalRequest) req;
        DataApprovalRestResponse approvalServletResponse = new DataApprovalRestResponse();

        if (RETRIEVE_APPROVAL_STEPS_ACTION.equals(approvalServletRequest.getAction())) {
            List<ApprovalStep> steps = approvalstepsDAO
                    .listByApprovalGroup(approvalServletRequest.approvalGroupId);
            List<ApprovalStepDTO> stepsDTO = new ArrayList<ApprovalStepDTO>();
            for (ApprovalStep step : steps) {
                stepsDTO.add(new ApprovalStepDTO(step));
            }
            approvalServletResponse.dataApprovalList = stepsDTO;

            return approvalServletResponse;
        }
        return null;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        new ObjectMapper().writeValue(getResponse().getWriter(), resp);
    }

}
