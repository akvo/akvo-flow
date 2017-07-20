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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
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
import com.gallatinsystems.survey.dao.ApprovalStepDAO;
import com.gallatinsystems.survey.dao.DataPointApprovalDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.ApprovalStep;
import com.gallatinsystems.survey.domain.DataPointApproval;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

import static org.waterforpeople.mapping.app.web.dto.SurveyInstanceRequest.*;

public class SurveyInstanceServlet extends AbstractRestApiServlet {

    private static final String UUID = "UUID";
    private static final String GEO = "GEO";
    private static final long serialVersionUID = -7690514561766005021L;
    private SurveyInstanceDAO surveyInstanceDao;
    private DataPointApprovalDAO approvalDao;
    private ApprovalStepDAO approvalStepDao;

    public SurveyInstanceServlet() {
        setMode(JSON_MODE);
        surveyInstanceDao = new SurveyInstanceDAO();
        approvalDao = new DataPointApprovalDAO();
        approvalStepDao = new ApprovalStepDAO();
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

        SurveyInstance si = surveyInstanceDao.getByKey(surveyInstanceId);
        
        //reassemble the displayname in case it has changed
        //TODO never store it at all
        si.setSurveyedLocaleDisplayName(makeDatapointName(si));

        SurveyInstanceDto siDto = new SurveyInstanceDto();
        BeanUtils.copyProperties(si, siDto);
        siDto.setKeyId(si.getKey().getId());

        InstanceDataDto instanceData = new InstanceDataDto();
        instanceData.surveyInstanceData = siDto;

        if (si.getSurveyedLocaleId() != null) {
            instanceData.latestApprovalStatus = retrieveDataPointApprovalStatus(si
                    .getSurveyedLocaleId());
        }
        return instanceData;
    }
    
    private String makeDatapointName(SurveyInstance si) {
        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final QuestionDao qDao = new QuestionDao();
        final SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        final QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();

        Long surveyedLocaleId = si.getSurveyedLocaleId();
        SurveyedLocale sl = slDao.getById(surveyedLocaleId);
        if (sl == null || sl.getCreationSurveyId() == null) {
            return "";
        }
        
        //Get the questions of the registration survey
        Long regSurveyId = sl.getCreationSurveyId();
        List<Question> nameQuestions = qDao.listDisplayNameQuestionsBySurveyId(regSurveyId);
        if (nameQuestions == null) {
            return "";
        }
        //Now get the answers for the instance that made the SL
        SurveyInstance regSurveyInstance = siDao.getRegistrationSurveyInstance(sl, regSurveyId);
        if (regSurveyInstance == null) {
            return "";
        }
        List<QuestionAnswerStore> responses = qasDao.listBySurveyInstance(regSurveyInstance.getKey().getId());
        //Put it all together
        sl.assembleDisplayName(nameQuestions, responses); //reuse existing method
        return sl.getDisplayName();        
    }

    private String retrieveDataPointApprovalStatus(Long surveyedLocaleId) {
        List<DataPointApproval> approvals = approvalDao.listBySurveyedLocaleId(surveyedLocaleId);
        List<Long> approvalStepIds = extractApprovalStepIds(approvals);
        ApprovalStep latestApprovalStep = retrieveLatestApprovalStep(approvalStepIds);

        return buildLatestApprovalStatus(latestApprovalStep, approvals);
    }

    private List<Long> extractApprovalStepIds(List<DataPointApproval> approvals) {
        List<Long> stepIds = new ArrayList<>();
        for (DataPointApproval approval : approvals) {
            stepIds.add(approval.getApprovalStepId());
        }
        return stepIds;
    }

    private ApprovalStep retrieveLatestApprovalStep(List<Long> approvalStepIds) {
        ApprovalStep latestApprovalStep = null;

        for (ApprovalStep step : approvalStepDao.listByKeys(approvalStepIds)) {
            if (latestApprovalStep == null || latestApprovalStep.getOrder() < step.getOrder()) {
                latestApprovalStep = step;
            }
        }

        return latestApprovalStep;
    }

    private String buildLatestApprovalStatus(ApprovalStep latestApprovalStep,
            List<DataPointApproval> approvals) {
        StringBuilder latestApprovalStatus = new StringBuilder();

        if (latestApprovalStep != null || !approvals.isEmpty()) {
            for (DataPointApproval approval : approvals) {
                if (approval.getApprovalStepId().equals(latestApprovalStep.getKey().getId())) {
                    latestApprovalStatus.append(latestApprovalStep.getTitle()).append(" - ")
                            .append(approval.getStatus());
                }
            }
        }

        return latestApprovalStatus.toString();
    }

    @Override
    protected void writeOkResponse(RestResponse response) throws Exception {
        getResponse().setStatus(200);
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
        jsonMapper.writeValue(getResponse().getWriter(), response);
    }
}
