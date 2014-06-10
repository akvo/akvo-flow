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
import com.gallatinsystems.framework.rest.exception.RestValidationException;

/**
 * data structure for rest api calls to the point of interest service
 * 
 * @author Christopher Fagiani
 */
public class PointOfInterestRequest extends RestRequest {
    private static final long serialVersionUID = 2511688888372190068L;
    private static final String LAT_PARAM = "lat";
    private static final String LON_PARAM = "lon";
    private static final String COUNTRY_PARAM = "country";

    private Double lat;
    private Double lon;
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    protected void populateErrors() {
        if (country == null && lat == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, LAT_PARAM
                            + " cannot be null if no " + COUNTRY_PARAM
                            + " is supplied"));
        }
        if (country == null && lon == null) {
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, LON_PARAM
                            + " cannot be null if no " + COUNTRY_PARAM
                            + " is supplied"));
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
        try {
            if (req.getParameter(LAT_PARAM) != null
                    && req.getParameter(LON_PARAM) != null) {
                lat = Double.parseDouble(req.getParameter(LAT_PARAM));
                lon = Double.parseDouble(req.getParameter(LON_PARAM));
            }
        } catch (NumberFormatException e) {
            throw new RestValidationException(
                    new RestError(RestError.BAD_DATATYPE_CODE,
                            RestError.BAD_DATATYPE_MESSAGE,
                            "lat, lon must be doubles"),
                    "Lat/lon must be doubles", e);
        }
    }
}
