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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.AccessPointMetricSummarizer;

public class AccessPointMetricProcessor extends HttpServlet {
    private static final Logger log = Logger
            .getLogger(AccessPointMetricProcessor.class.getName());
    /**
	 * 
	 */
    private static final long serialVersionUID = 4375353186699165558L;
    static Integer processcount = 0;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String key = req.getParameter("key");
        AccessPointMetricSummarizer apms = new AccessPointMetricSummarizer();
        apms.performSummarization(key, null, null, null, null);
        ++processcount;
        if (processcount % 50 == 0)
            log.log(Level.INFO, "Processed : " + processcount);
        resp.setStatus(200);
    }

}
