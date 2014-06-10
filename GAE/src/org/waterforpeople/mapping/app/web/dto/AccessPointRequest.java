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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.exception.RestValidationException;

/**
 * data structure for rest api calls to the access point service
 * 
 * @author Christopher Fagiani
 */
public class AccessPointRequest extends RestRequest {
    private static final long serialVersionUID = 2511688888372190068L;
    private static final String LAT_PARAM = "lat";
    private static final String LON_PARAM = "lon";
    private static final String COUNTRY_PARAM = "country";
    private static final String COMM_PARAM = "community";
    private static final String CONST_DATE_FROM_PARAM = "constructionDateFrom";
    private static final String CONST_DATE_TO_PARAM = "constructionDateTo";
    private static final String COLL_DATE_FROM_PARAM = "collectionDateFrom";
    private static final String COLL_DATE_TO_PARAM = "collectionDateTo";
    private static final String TYPE_PARAM = "pointType";
    public static final String NEARBY_ACTION = "getnearby";
    public static final String SEARCH_ACTION = "search";

    private static final ThreadLocal<DateFormat> DATE_FMT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return DateFormat.getDateInstance();
        };
    };

    private Double lat;
    private Double lon;
    private String country;
    private String community;
    private Date constructionDateFrom;
    private Date constructionDateTo;
    private Date collectionDateFrom;
    private Date collectionDateTo;
    private String type;

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Date getConstructionDateFrom() {
        return constructionDateFrom;
    }

    public void setConstructionDateFrom(Date constructionDateFrom) {
        this.constructionDateFrom = constructionDateFrom;
    }

    public Date getConstructionDateTo() {
        return constructionDateTo;
    }

    public void setConstructionDateTo(Date constructionDateTo) {
        this.constructionDateTo = constructionDateTo;
    }

    public Date getCollectionDateFrom() {
        return collectionDateFrom;
    }

    public void setCollectionDateFrom(Date collectionDateFrom) {
        this.collectionDateFrom = collectionDateFrom;
    }

    public Date getCollectionDateTo() {
        return collectionDateTo;
    }

    public void setCollectionDateTo(Date collectionDateTo) {
        this.collectionDateTo = collectionDateTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
        if (NEARBY_ACTION.equalsIgnoreCase(getAction())) {
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
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        country = req.getParameter(COUNTRY_PARAM);
        community = req.getParameter(COMM_PARAM);
        type = req.getParameter(TYPE_PARAM);
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
        try {
            collectionDateFrom = parseDate(req
                    .getParameter(COLL_DATE_FROM_PARAM));
            collectionDateTo = parseDate(req.getParameter(COLL_DATE_TO_PARAM));
            constructionDateFrom = parseDate(req
                    .getParameter(CONST_DATE_FROM_PARAM));
            constructionDateTo = parseDate(req
                    .getParameter(CONST_DATE_TO_PARAM));
        } catch (Exception e) {
            throw new RestValidationException(new RestError(
                    RestError.BAD_DATATYPE_CODE,
                    RestError.BAD_DATATYPE_MESSAGE, "Cannot parse date"),
                    "Cannot parse date", e);
        }
    }

    private Date parseDate(String val) throws ParseException {
        Date date = null;
        if (val != null && val.trim().length() > 0) {
            date = DATE_FMT.get().parse(val);
        }
        return date;
    }
}
