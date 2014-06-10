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

package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request for GeoServlet calls
 * 
 * @author Christopher Fagiani
 */
public class GeoRequest extends RestRequest {
    private static final long serialVersionUID = 3938671447495497433L;

    public static final String LIST_COUNTRY_ACTION = "getCountries";
    public static final String LIST_COMMUNITY_ACTION = "getCommunities";
    public static final String COUNTRY_PARAM = "country";
    public static final String MAP_TYPE_PARAM = "mapType";
    public static final String PUBLIC_MAP_TYPE = "public";
    public static final String KMZ_MAP_TYPE = "kmz";
    private String country;
    private String mapType;

    @Override
    protected void populateErrors() {
        if (LIST_COMMUNITY_ACTION.equals(getAction()) && country == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, COUNTRY_PARAM));
        }
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        country = req.getParameter(COUNTRY_PARAM);
        if (country != null) {
            country = country.trim().toUpperCase();
            if (country.length() == 0) {
                country = null;
            }
        }
        mapType = req.getParameter(MAP_TYPE_PARAM);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

}
