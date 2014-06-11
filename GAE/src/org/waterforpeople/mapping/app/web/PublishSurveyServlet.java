/*
 *  Copyright (C) 2013 Stichting Akvo (Akvo Foundation)
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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;

public class PublishSurveyServlet extends HttpServlet {

    private static final long serialVersionUID = -8625535651028940339L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final String surveyId = req.getParameter("surveyId");
        final PrintWriter pw = resp.getWriter();
        if (surveyId == null || "".equals(surveyId)) {
            pw.write("surveyId parameter is required");
            return;
        }
        new SurveyServiceImpl().publishSurveyAsync(Long.valueOf(surveyId));
        pw.write("Request processed. Check [View Messages] in the Dashboard");
    }

}
