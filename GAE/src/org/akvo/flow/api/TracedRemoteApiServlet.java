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

package org.akvo.flow.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.apphosting.utils.remoteapi.RemoteApiServlet;

/*
 * A class used for tracing/grouping Remote API calls
 * 
 * Rationale: We want to be able to group/trace/correlate a set of Remote API calls,
 * to achieve this the client _must_ configure the RemoteApiOptions.remoteApiPath to be a unique
 * identifier for grouping requests, e.g. /traced_remote_api/uuid-1
 * Then we should be able in the logs to filter by a particular set as 
 * `path:/traced_remote_api/uuid-1` to get the relative cost of a given execution
 * 
 * The API calls are just delegated to the default RemoteApiServlet
 */
public class TracedRemoteApiServlet extends RemoteApiServlet {

    private static final long serialVersionUID = 1762368166833558193L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.doGet(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.doPost(request, response);
    }

}
