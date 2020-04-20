/*
 *  Copyright (C) 2010-2015,2019-2020 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.surveyal.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

/**
 * Data access object for manipulating SurveyedLocales
 *
 * @author Christopher Fagiani
 */
public class SurveyedLocaleDao extends BaseDAO<SurveyedLocale> {

    public SurveyedLocaleDao() {
        super(SurveyedLocale.class);
    }

    /**
     * lists the set of SurveyedLocales that are within tolerance of the lat/lon coordinates passed
     * in.
     *
     * @param lat
     * @param lon
     * @param tolerance
     * @return
     */
    public List<SurveyedLocale> listLocalesByCoordinates(String pointType,
            double lat, double lon, double tolerance) {
        return listLocalesByCoordinates(pointType, lat - tolerance, lon
                - tolerance, lat + tolerance, lon + tolerance,
                CURSOR_TYPE.all.toString(), null);
    }

    /**
     * lists locales that fit within the bounding box geocells passed in
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listLocalesByGeocell(Long surveyId, List<String> geocells, int pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "surveyGroupId == :p1 && :p2.contains(geocells)";
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class, queryString);
        prepareCursor(null, pageSize, query);
        List<SurveyedLocale> results = (List<SurveyedLocale>) query.execute(surveyId, geocells);
        return results;
    }


    /**
     * lists all locales
     *
     * @param cursor
     * @param pagesize
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listAll(String cursor, Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
        prepareCursor(cursor, pageSize, query);
        List<SurveyedLocale> results = (List<SurveyedLocale>) query
                .execute();
        return results;
    }

    /**
     * lists locales that fit within the bounding box passed in
     *
     * @param pointType
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listLocalesByCoordinates(String pointType,
            Double lat1, Double lon1, Double lat2, Double lon2, String cursor,
            Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("localeType", filterString, paramString, "String",
                pointType, paramMap);
        appendNonNullParam("latitude", filterString, paramString, "Double",
                lat1, paramMap, GTE_OP);
        appendNonNullParam("latitude", filterString, paramString, "Double",
                lat2, paramMap, LTE_OP);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        prepareCursor(cursor, pageSize, query);
        List<SurveyedLocale> candidates = (List<SurveyedLocale>) query
                .executeWithMap(paramMap);
        // since the datastore only supports an inequality check on a single
        // parameter at a time, only look at LAT in the query. Filter on Lon
        // afterwards.
        List<SurveyedLocale> results = new ArrayList<SurveyedLocale>();
        if (candidates != null) {
            for (SurveyedLocale l : candidates) {
                if (l.getLongitude() > (lon1) && l.getLongitude() < (lon2)) {
                    results.add(l);
                }
            }
        }
        return results;
    }

    /**
     * lists all locales that match the geo constraints passed in
     *
     * @param countryCode
     * @param level
     * @param subValue
     * @param type
     * @param org
     * @param cursor
     * @param desiredResults
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listBySubLevel(String countryCode,
            Integer level, String subValue, String type, String org,
            String cursor, Integer desiredResults) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("localeType", filterString, paramString, "String",
                type, paramMap);
        appendNonNullParam("countryCode", filterString, paramString, "String",
                countryCode, paramMap);
        appendNonNullParam("organization", filterString, paramString, "String",
                org, paramMap);
        if (level != null && level > 0 && level <= 6) {
            appendNonNullParam("sublevel" + level, filterString, paramString,
                    "String", subValue, paramMap);
        }
        query.setOrdering("createdDateTime desc");
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        prepareCursor(cursor, desiredResults, query);
        List<SurveyedLocale> results = (List<SurveyedLocale>) query
                .executeWithMap(paramMap);
        return results;
    }

    /**
     * searches for surveyedLocale based on params passed in
     *
     * @param country
     * @param collDateFrom
     * @param collDateTo
     * @param type
     * @param metricId
     * @param metricValue
     * @param orderByField
     * @param orderByDir
     * @param pageSize
     * @param cursorString
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> search(String country, Date collDateFrom,
            Date collDateTo, String type, String orderByField,
            String orderByDir, Integer pageSize, String cursorString) {

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        appendNonNullParam("countryCode", filterString, paramString, "String",
                country, paramMap);

        appendNonNullParam("localeType", filterString, paramString, "String",
                type, paramMap);

        appendNonNullParam("lastSurveyedDate", filterString, paramString,
                "Date", collDateFrom, paramMap, GTE_OP);
        appendNonNullParam("lastSurveyedDate", filterString, paramString,
                "Date", collDateTo, paramMap, LTE_OP);

        if (orderByField != null) {
            String ordering = orderByDir;
            if (ordering == null) {
                ordering = "asc";
            }
            query.setOrdering(orderByField + " " + ordering);
        }
        if (filterString.length() > 0) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
        }
        prepareCursor(cursorString, pageSize, query);
        if (collDateFrom != null || collDateTo != null) {
            query.declareImports("import java.util.Date");
            if (orderByField != null
                    && !orderByField.trim().equals("lastSurveyedDate")) {
                query.setOrdering("lastSurveyedDate "
                        + (orderByDir != null ? orderByDir : "asc"));
            }
        }
        return (List<SurveyedLocale>) query.executeWithMap(paramMap);
    }

    /**
     * returns all the locales by surveyGroupId survey instance only.
     *
     * @param surveyGroupId
     * @return
     */
    public List<SurveyedLocale> listLocalesBySurveyGroupId(Long surveyGroupId) {
        List<SurveyedLocale> locales = listByProperty("surveyGroupId", surveyGroupId,
                "Long");
        return locales;
    }

    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listSurveyedLocales(String cursor, Long surveyGroupId,
            String identifier, String displayName, String displayNamePrefix) {

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("surveyGroupId", filterString, paramString, "Long",
                surveyGroupId, paramMap);
        appendNonNullParam("identifier", filterString, paramString,
                "String", identifier, paramMap);
        appendNonNullParam("displayName", filterString, paramString,
                "String", displayName, paramMap);
        if (displayNamePrefix != null && !"".equals(displayNamePrefix)) {
            String endString = displayNamePrefix + Character.MAX_VALUE;
            appendNonNullParam("displayName", filterString, paramString,
                    "String", displayNamePrefix, paramMap, GTE_OP);
            appendNonNullParam("displayName", filterString, paramString,
                    "String", endString, paramMap, LT_OP);
            query.setOrdering("displayName asc"); //necessary if inequality ops present
            //Alternative custom filterString:
            // surveyGroupId == surveyGroupIdParam0 && displayName.startsWith(displayNameParam1)
        } else {
            query.setOrdering("lastUpdateDateTime desc");
        }



        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        if (cursor != null && !"".equals(cursor))
            prepareCursor(cursor, query);

        return (List<SurveyedLocale>) query.executeWithMap(paramMap);

    }


    public List<SurveyedLocale> listLocalesByDisplayName(String displayName) {
        List<SurveyedLocale> locales = listByProperty("displayName", displayName, "String");
        return locales;
    }

    /**
     * returns all the locales by surveyGroupId, from a certain date. If no date is supplised, t = 0
     * is used.
     *
     * @param surveyGroupId
     * @param lastUpdateTime
     * @param pageSize
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listLocalesBySurveyGroupAndDate(Long surveyGroupId,
            Date lastUpdateTime, Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();

        appendNonNullParam("surveyGroupId", filterString, paramString, "Long",
                surveyGroupId, paramMap);
        appendNonNullParam("lastUpdateDateTime", filterString, paramString, "Date",
                lastUpdateTime, paramMap, " >= ");

        query.setOrdering("lastUpdateDateTime asc");
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        query.declareImports("import java.util.Date");
        query.setRange(0, pageSize);

        return (List<SurveyedLocale>) query.executeWithMap(paramMap);
    }

    public SurveyedLocale getById(Long id) {
        final SurveyedLocale sl = getByKey(id);
        return sl;
    }

    /**
     * finds a single surveyedLocale by identifier.
     *
     * @param identifier
     * @return
     */
    public SurveyedLocale getByIdentifier(String identifier) {
        return findByProperty("identifier", identifier, "String");
    }

    @SuppressWarnings("unchecked")
    public List<SurveyedLocale> listLocalesByCreationSurvey(Long surveyId,
            String cursor, Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "creationSurveyId == surveyIdParam";
        javax.jdo.Query query = pm.newQuery(SurveyedLocale.class, queryString);
        query.declareParameters("Long surveyIdParam");
        prepareCursor(cursor, pageSize, query);
        List<SurveyedLocale> results = (List<SurveyedLocale>) query.execute(surveyId);
        if (results != null && results.size() > 0) {
            return results;
        } else {
            return Collections.emptyList();
        }
    }

    // TODO do this in a task, to buy more time
    public void deleteSurveyedLocale(SurveyedLocale sl) {
        if (sl != null) {
            // get the list of all surveyInstances, and delete them first
            List<SurveyInstance> siList = new ArrayList<SurveyInstance>();
            SurveyInstanceDAO siDao = new SurveyInstanceDAO();
            siList = siDao.listInstancesByLocale(sl.getKey().getId(), null, null, null);
            List<Long> ids = new ArrayList<Long>();

            // delete all containing surveyInstances
            for (SurveyInstance si : siList) {
                ids.add(si.getSurveyId());
                siDao.deleteSurveyInstance(si);
            }
            // The datapoint will be automatically deleted
            // once all contained surveyInstances are deleted.

            // notify flow services
            SurveyUtils.notifyReportService(ids, "invalidate");
        }
    }

    public long countBySurveyGroupId(long surveyGroupId) {
        return countFilteredByProperty("surveyGroupId", surveyGroupId);
    }
}
