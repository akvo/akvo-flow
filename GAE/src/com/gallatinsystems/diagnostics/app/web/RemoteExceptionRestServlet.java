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

package com.gallatinsystems.diagnostics.app.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.diagnostics.app.web.dto.RemoteExceptionRequest;
import com.gallatinsystems.diagnostics.dao.RemoteStacktraceDao;
import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.framework.rest.exception.RestException;
import com.google.appengine.api.datastore.Text;

/**
 * servlet for saving stack traces posted by the devices
 * 
 * @author Christopher Fagiani
 */
public class RemoteExceptionRestServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = 1831040260541847041L;
    private RemoteStacktraceDao stacktraceDao;

    public RemoteExceptionRestServlet() {
        stacktraceDao = new RemoteStacktraceDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest exReq = new RemoteExceptionRequest();
        exReq.populateFromHttpRequest(req);
        return exReq;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse resp = new RestResponse();
        RemoteExceptionRequest exReq = (RemoteExceptionRequest) req;
        // saves a stacktrace
        if (RemoteExceptionRequest.SAVE_TRACE_ACTION.equals(req.getAction())) {
            RemoteStacktrace trace = new RemoteStacktrace();
            trace.setErrorDate(exReq.getDate() != null ? exReq.getDate()
                    : new Date());
            trace.setSoftwareVersion(exReq.getVersion());
            trace.setDeviceIdentifier(exReq.getDeviceIdent());
            trace.setPhoneNumber(exReq.getPhoneNumber());
            trace.setStackTrace(new Text(exReq.getStackTrace()));
            stacktraceDao.save(trace);
        } else {
            throw new RestException(new RestError(RestError.BAD_DATATYPE_CODE,
                    RestError.BAD_DATATYPE_MESSAGE, "Action: "
                            + req.getAction() + " not supported"),
                    "Bad Action value", null);
        }
        return resp;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }

}
