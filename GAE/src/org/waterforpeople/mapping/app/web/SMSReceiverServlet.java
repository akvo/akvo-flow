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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.SMSRestRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.sms.dao.SMSMessageDao;
import com.gallatinsystems.sms.domain.SMSMessage;

/**
 * this servlet will process incoming messages forwarded by an SMS gateway
 * 
 * @author Christopher Fagiani
 */
public class SMSReceiverServlet extends AbstractRestApiServlet {

    private static final long serialVersionUID = 4059925726791884167L;
    private static final Map<String, AccessPoint.Status> STATUS_MAP = new HashMap<String, AccessPoint.Status>() {
        private static final long serialVersionUID = -549152350427462282L;

        {
            put("A", Status.FUNCTIONING_HIGH);
            put("B", Status.FUNCTIONING_WITH_PROBLEMS);
            put("C", Status.BROKEN_DOWN);
        }
    };

    private AccessPointDao apDao;
    private SMSMessageDao smsDao;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SMSRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SMSRestRequest smsReq = (SMSRestRequest) req;
        SMSMessage message = new SMSMessage();
        message.setFrom(smsReq.getFrom());
        message.setText(smsReq.getText());
        message.setSentDate(smsReq.getTimestamp());
        smsDao.save(message);
        String[] parts = message.getText().split(" ");
        if (parts.length == 2) {
            AccessPoint ap = apDao.findAccessPointBySMSCode(parts[0]);
            if (ap != null) {
                Status status = STATUS_MAP.get(parts[1]);
                if (status != null) {
                    ap.setPointStatus(status);
                    apDao.save(ap);
                }
            }
        }
        RestResponse resp = new RestResponse();
        resp.setCode("200");
        return resp;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        resp.setCode("200");
    }

}
