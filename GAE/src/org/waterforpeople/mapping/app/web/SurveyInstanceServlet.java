/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceResponse;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class SurveyInstanceServlet extends AbstractRestApiServlet {

    private static final String UUID = "UUID";
    private static final String GEO = "GEO";
    /**
	 * 
	 */
    private static final long serialVersionUID = -7690514561766005021L;

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
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        if (siReq.getFieldName().equalsIgnoreCase(GEO)) {
            List<QuestionAnswerStore> qasList = qasDao.listByTypeValue(siReq.getFieldName(),
                    siReq.getValue());
            if (qasList != null && qasList.size() > 0) {
                SurveyInstanceResponse sir = new SurveyInstanceResponse();
                sir.setSurveyInstanceId(qasList.get(0).getSurveyInstanceId());
                sir.setCreatedDateTime(qasList.get(0).getCreatedDateTime());
                return sir;
            }
        } else if (siReq.getFieldName().equalsIgnoreCase(UUID)) {
            SurveyInstance si = siDao.findByUUID(siReq.getValue());
            if (si != null) {
                SurveyInstanceResponse sir = new SurveyInstanceResponse();
                sir.setSurveyInstanceId(si.getKey().getId());
                sir.setCreatedDateTime(si.getCreatedDateTime());
                return sir;
            }
        }
        SurveyInstanceResponse sir = new SurveyInstanceResponse();
        sir.setSurveyInstanceId(null);
        sir.setCreatedDateTime(null);
        return sir;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        SurveyInstanceResponse sir = (SurveyInstanceResponse) resp;
        JSONObject result = new JSONObject(sir);
        getResponse().getWriter().println(result.toString());
    }

}
