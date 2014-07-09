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

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.MapAssemblyRestRequest;
import org.waterforpeople.mapping.helper.KMLHelper;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class MapAssemblyServlet extends AbstractRestApiServlet {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MapAssemblyServlet.class
            .getName());

    private static final long serialVersionUID = 7615652730572144228L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new MapAssemblyRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        MapAssemblyRestRequest importReq = (MapAssemblyRestRequest) req;
        KMLHelper kmlHelper = new KMLHelper();

        if ("buildMap".equals(importReq.getAction())) {
            kmlHelper.buildMap();
        } else if (Constants.BUILD_COUNTRY_FRAGMENTS.equals(importReq
                .getAction())) {
            String countryCode = importReq.getCountryCode();
            if (countryCode != null)
                kmlHelper.buildCountryFragments(countryCode);
        } else if (Constants.BUILD_COUNTRY_TECH_TYPE_FRAGMENTS.equals(importReq
                .getAction())) {
            String countryCode = importReq.getCountryCode();
            String techType = importReq.getTechType();
            if (techType != null && countryCode != null)
                kmlHelper.buildCountryTechTypeFragment(countryCode, techType);
        }
        return null;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // no-op

    }

}
