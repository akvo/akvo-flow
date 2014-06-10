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

package com.gallatinsystems.standards.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.StandardScoring;

/**
 * Dao for managing scoring objects. TODO: need to move this or get rid of dependency on AccessPoint
 */
public class StandardScoringDao extends BaseDAO<StandardScoring> {

    public StandardScoringDao() {
        super(StandardScoring.class);
    }

    public List<StandardScoring> listStandardScoring(AccessPoint ap) {
        List<StandardScoring> ssList = new ArrayList<StandardScoring>();
        ssList = super.listByProperty("pointType", ap.getPointType().toString(), "String");
        return ssList;
    }

    public List<StandardScoring> listStandardScoringByBucketForAccessPoint(
            Long scoreBucketId, AccessPoint ap) {
        List<StandardScoring> supersetList = new ArrayList<StandardScoring>();

        List<StandardScoring> globalList = listGlobalStandardScoringForAccessPoint(
                scoreBucketId, ap);
        List<StandardScoring> localList = listLocalStandardScoringForAccessPoint(scoreBucketId, ap);
        Collections.copy(supersetList, globalList);
        Collections.copy(supersetList, localList);
        return supersetList;
    }

    public List<StandardScoring> listStandardScoring(Long scoreBucketId) {
        List<StandardScoring> ssList = new ArrayList<StandardScoring>();
        ssList = super.listByProperty("scoreBucketId", scoreBucketId, "Long");
        return ssList;
    }

    @SuppressWarnings("unchecked")
    public List<StandardScoring> listGlobalStandardScoringForAccessPoint(
            Long scoreBucketId, AccessPoint ap) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPoint.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("pointType", filterString, paramString, "String",
                ap.getPointType(), paramMap);
        appendNonNullParam("scoreBucketId", filterString, paramString, "Long",
                scoreBucketId, paramMap);
        appendNonNullParam("mapToObject", filterString, paramString, "String",
                "AccessPoint", paramMap);
        appendNonNullParam("scopeType", filterString, paramString, "String",
                "GLOBAL", paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<StandardScoring> results = (List<StandardScoring>) query
                .executeWithMap(paramMap);
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<StandardScoring> listLocalDistanceStandardScoringForAccessPoint(
            AccessPoint ap) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPoint.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("pointType", filterString, paramString, "String",
                ap.getPointType(), paramMap);
        appendNonNullParam("mapToObject", filterString, paramString, "String",
                "AccessPoint", paramMap);
        appendNonNullParam("criteriaType", filterString, paramString, "String",
                "Distance", paramMap);
        appendNonNullParam("countryCode", filterString, paramString, "String",
                ap.getCountryCode(), paramMap);
        // ToDo: need to think about how to use the subvalue
        // appendNonNullParam("subValue", filterString, paramString, "String",
        // ap.getSub1(), paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<StandardScoring> results = (List<StandardScoring>) query
                .executeWithMap(paramMap);
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<StandardScoring> listLocalStandardScoringForAccessPoint(
            Long scoreBucketId, AccessPoint ap) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPoint.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("pointType", filterString, paramString, "String",
                ap.getPointType(), paramMap);
        appendNonNullParam("scoreBucketId", filterString, paramString, "Long",
                scoreBucketId, paramMap);
        appendNonNullParam("mapToObject", filterString, paramString, "String",
                "AccessPoint", paramMap);
        appendNonNullParam("scopeType", filterString, paramString, "String",
                "LOCAL", paramMap);
        appendNonNullParam("countryCode", filterString, paramString, "String",
                ap.getCountryCode(), paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<StandardScoring> results = (List<StandardScoring>) query
                .executeWithMap(paramMap);
        return results;
    }

}
