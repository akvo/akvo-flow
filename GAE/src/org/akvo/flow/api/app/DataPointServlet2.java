/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import org.akvo.flow.util.FlowJsonObjectWriter;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;


/**
 * JSON service for returning the list of assigned data point records for a specific device and surveyId
 */
@SuppressWarnings("serial")
public class DataPointServlet2 extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(DataPointServlet2.class.getName());

    public DataPointServlet2() {
        setMode(JSON_MODE);
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DataPointRequest();
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
        DataPointRequest dpReq = (DataPointRequest) req;
        DataPointResponse res = new DataPointResponse();
        if (dpReq.getSurveyId() == null) {
            res.setCode(String.valueOf(HttpServletResponse.SC_FORBIDDEN));
            res.setMessage("Invalid Survey");
            return res;
        }

        DataPointUtil dpu = new DataPointUtil();
        List<SurveyedLocale> dpList;
        try {
             dpList = dpu.getAssignedDataPoints(dpReq.getAndroidId(), dpReq.getSurveyId(), dpReq.getCursor());
        } catch(Exception e) {
            res.setCode(String.valueOf(HttpServletResponse.SC_NOT_FOUND));
            res.setMessage(e.getMessage());
            return res;
        }

        res = convertToResponse(dpList);
        res.setCursor(BaseDAO.getCursor(dpList));

        return res;
    }


    /**
     * converts the domain objects to dtos and then installs them in a DataPointResponse object
     */
    private DataPointResponse convertToResponse(List<SurveyedLocale> slList) {
        DataPointResponse resp = new DataPointResponse();
        if (slList == null) {
            resp.setCode(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            resp.setMessage("Internal Server Error");
            return resp;
        }
        // set meta data
        resp.setCode(String.valueOf(HttpServletResponse.SC_OK));
        resp.setResultCount(slList.size());

        DataPointUtil dpu = new DataPointUtil();
        List<SurveyedLocaleDto> dtoList = dpu.getSimpleSurveyedLocaleDtosList(slList);

        resp.setDataPointData(dtoList);
        return resp;
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
            OutputStream stream = getResponse().getOutputStream();
            writer.writeValue(stream, resp);
            PrintWriter endwriter = new PrintWriter(stream);
            endwriter.println();
        } else {
            getResponse().getWriter().println(resp.getMessage());
        }
    }
}
