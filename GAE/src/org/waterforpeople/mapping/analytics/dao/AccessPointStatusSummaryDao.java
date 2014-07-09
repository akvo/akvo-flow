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

package org.waterforpeople.mapping.analytics.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.geography.domain.Country;
import com.google.appengine.api.datastore.DatastoreTimeoutException;

/**
 * updates access point status summary objects
 * 
 * @author Christopher Fagiani
 */
public class AccessPointStatusSummaryDao extends
        BaseDAO<AccessPointStatusSummary> {

    public AccessPointStatusSummaryDao() {
        super(AccessPointStatusSummary.class);
    }

    /**
     * synchronized static method so that only 1 thread can be updating a summary at a time. This is
     * inefficient but is the only way we can be sure we're keeping the count consistent since there
     * is no "select for update" or sql dml-like construct
     * 
     * @param answer
     */
    @SuppressWarnings("rawtypes")
    public static synchronized void incrementCount(AccessPoint ap, Country c,
            int unit) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPointStatusSummary.class);
        query
                .setFilter("year == yearParam && status == statusParam && community == communityParam && type == typeParam");
        query
                .declareParameters("String yearParam, String statusParam, String communityParam, String typeParam");
        String yearString = null;
        if (ap.getCollectionDate() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(ap.getCollectionDate());
            yearString = cal.get(Calendar.YEAR) + "";
        }
        List results = (List) query.executeWithArray(yearString, ap
                .getPointStatus(), ap.getCommunityCode(), ap.getPointType());
        AccessPointStatusSummary summary = null;
        if ((results == null || results.size() == 0) && unit > 0) {
            summary = new AccessPointStatusSummary();
            summary.setCount(1L);
            summary.setYear(yearString);
            summary.setStatus(ap.getPointStatus());
            if (ap.getCountryCode() != null) {
                summary.setCountry(ap.getCountryCode());
            } else {
                summary.setCountry(c.getIsoAlpha2Code());
            }
            summary.setCommunity(ap.getCommunityCode());
            summary.setType(ap.getPointType() != null ? ap.getPointType()
                    .toString() : "UNKNOWN");
        } else if (unit > 0) {
            summary = (AccessPointStatusSummary) results.get(0);
            summary.setCount(summary.getCount() + unit);
        }
        if (summary != null) {
            AccessPointStatusSummaryDao thisDao = new AccessPointStatusSummaryDao();
            if (summary.getCount() == 0 && summary.getKey() != null) {
                thisDao.delete(summary);
            } else if (summary.getCount() > 0) {
                try {
                    thisDao.save(summary);
                } catch (DatastoreTimeoutException te) {
                    sleep();
                    thisDao.save(summary);
                }
            }
        }
    }

    /**
     * lists access point summary objects that match the criteria passed in, any of which are
     * nullable.
     * 
     * @param country
     * @param community
     * @param year
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AccessPointStatusSummary> listByLocationAndYear(String country,
            String community, String type, String year, String status) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPointStatusSummary.class);

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        appendNonNullParam("country", filterString, paramString, "String",
                country, paramMap);
        appendNonNullParam("community", filterString, paramString, "String",
                community, paramMap);
        appendNonNullParam("year", filterString, paramString, "String", year,
                paramMap);
        appendNonNullParam("type", filterString, paramString, "String", type,
                paramMap);
        appendNonNullParam("status", filterString, paramString, "String",
                status, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        return (List<AccessPointStatusSummary>) query.executeWithMap(paramMap);
    }

    /**
     * lists the summary objects for a country with a creation date on or after the data passed in
     * 
     * @param country
     * @param creationDate
     * @param cursorString
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AccessPointStatusSummary> listByCountryAndCreationDate(
            String country, Date creationDate, String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPointStatusSummary.class);
        query
                .setFilter("country == countryParam && createdDateTime > dateParam");
        query.declareParameters("String countryParam, Date dateParam");
        query.declareImports("import java.util.Date");
        prepareCursor(cursorString, query);
        return (List<AccessPointStatusSummary>) query.execute(country,
                creationDate);
    }

}
