/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.web.dto.InstanceDataDto;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceResponse;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

import static org.waterforpeople.mapping.app.web.dto.SurveyInstanceRequest.*;

public class SurveyInstanceServlet extends AbstractRestApiServlet {

    private static final String UUID = "UUID";
    private static final String GEO = "GEO";
    private static final long serialVersionUID = -7690514561766005021L;
    private SurveyInstanceDAO surveyInstanceDao;

    public SurveyInstanceServlet() {
        setMode(JSON_MODE);
        surveyInstanceDao = new SurveyInstanceDAO();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyInstanceRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SurveyInstanceRequest siReq = (SurveyInstanceRequest) req;
        SurveyInstanceDAO siDao = new SurveyInstanceDAO();

        if (GET_INSTANCE_DATA_ACTION.equals(siReq.getAction())) {
            return retrieveInstanceData(siReq.surveyInstanceId);
        } else {
            QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
            SurveyInstanceResponse sir = new SurveyInstanceResponse();
            if (GEO.equalsIgnoreCase(siReq.getFieldName())) {
                List<QuestionAnswerStore> qasList = qasDao.listByTypeValue(siReq.getFieldName(),
                        siReq.getValue());
                if (qasList != null && qasList.size() > 0) {
                    sir.setSurveyInstanceId(qasList.get(0).getSurveyInstanceId());
                    sir.setCreatedDateTime(qasList.get(0).getCreatedDateTime());
                }
            } else if (UUID.equalsIgnoreCase(siReq.getFieldName())) {
                SurveyInstance si = siDao.findByUUID(siReq.getValue());
                if (si != null) {
                    sir.setSurveyInstanceId(si.getKey().getId());
                    sir.setCreatedDateTime(si.getCreatedDateTime());
                }
            }
            return sir;
        }
    }

    private RestResponse retrieveInstanceData(Long surveyInstanceId) {
        InstanceDataDto instanceData = new InstanceDataDto();
        instanceData.surveyInstanceData = surveyInstanceDao.getByKey(surveyInstanceId);
        instanceData.latestApprovalStatus = retrieveSurveyInstanceApprovalStatus(surveyInstanceId);
        return instanceData;
    }

    private String retrieveSurveyInstanceApprovalStatus(Long surveyInstanceId) {
        return "";
    }

    @Override
    protected void writeOkResponse(RestResponse response) throws Exception {
        getResponse().setStatus(200);
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.writeValue(getResponse().getWriter(), response);
    }
}
