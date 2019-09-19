/*
 *  Copyright (C) 2010-2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.api.app;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import org.akvo.flow.util.FlowJsonObjectWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * JSON service for returning the list of records for a specific surveyId
 *
 * @author Mark Tiele Westra
 */
public class SurveyedLocaleServlet extends AbstractRestApiServlet {
    private static final long serialVersionUID = 8748650927754433019L;
    private SurveyedLocaleDao surveyedLocaleDao;
    private static final Integer SL_PAGE_SIZE = 30;

    public SurveyedLocaleServlet() {
        setMode(JSON_MODE);
        surveyedLocaleDao = new SurveyedLocaleDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyedLocaleRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    /**
     * calls the surveyedLocaleDao to get the list of surveyedLocales for a certain surveyGroupId
     * passed in via the request, or the total number of available surveyedLocales if the
     * checkAvailable flag is set.
     */
    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SurveyedLocaleRequest slReq = (SurveyedLocaleRequest) req;
        List<SurveyedLocale> slList;
        if (slReq.getSurveyGroupId() != null) {
            DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
            SurveyDAO surveyDao = new SurveyDAO();
            List<DeviceSurveyJobQueue> deviceSurveyJobQueues = dsjqDAO
                    .get(slReq.getPhoneNumber(), slReq.getImei(), slReq.getAndroidId());
            for (DeviceSurveyJobQueue dsjq : deviceSurveyJobQueues) {
                Survey s = surveyDao.getById(dsjq.getSurveyID());
                if (s != null && s.getSurveyGroupId().longValue()
                        == slReq.getSurveyGroupId().longValue()) {
                    slList = surveyedLocaleDao.listLocalesBySurveyGroupAndDate(
                            slReq.getSurveyGroupId(), slReq.getLastUpdateTime(), SL_PAGE_SIZE);
                    return DataPointServlet.convertToResponse(slList, slReq.getSurveyGroupId(), new DataPointResponse());
                }
            }
        }
        // A valid assignment has not been found for the given device
        RestResponse res = new RestResponse();
        res.setCode(String.valueOf(HttpServletResponse.SC_FORBIDDEN));
        res.setMessage("Invalid assignment");
        return res;
    }

    /**
     * writes response as a JSON string
     */
    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        int sc;
        try {
            sc = Integer.valueOf(resp.getCode());
        } catch (NumberFormatException ignored) {
            // Status code was not properly set in the RestResponse
            sc = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        getResponse().setStatus(sc);
        if (sc == HttpServletResponse.SC_OK) {
            FlowJsonObjectWriter writer = new FlowJsonObjectWriter();
            writer.writeValue(getResponse().getOutputStream(), resp);
            getResponse().getWriter().println();
        } else {
            getResponse().getWriter().println(resp.getMessage());
        }
    }
}
