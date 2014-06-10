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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.DistanceStandard;
import com.gallatinsystems.standards.domain.Standard;

public class DistanceStandardDao extends BaseDAO<DistanceStandard> {

    public DistanceStandardDao() {
        super(DistanceStandard.class);
    }

    @SuppressWarnings("unchecked")
    public List<DistanceStandard> listDistanceStandard(
            Standard.StandardType type, String countryCode) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DistanceStandard.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("standardType", filterString, paramString, "String",
                type.toString(), paramMap);
        appendNonNullParam("countryCode", filterString, paramString, "String",
                countryCode, paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<DistanceStandard> standardList = (List<DistanceStandard>) query
                .executeWithMap(paramMap);

        return standardList;

    }

    @SuppressWarnings("unchecked")
    public DistanceStandard findDistanceStandard(Standard.StandardType type,
            String countryCode, AccessPoint.LocationType locationType) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DistanceStandard.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("standardType", filterString, paramString, "String",
                type.toString(), paramMap);
        appendNonNullParam("countryCode", filterString, paramString, "String",
                countryCode, paramMap);
        appendNonNullParam("locationType", filterString, paramString, "String",
                locationType, paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        List<DistanceStandard> standardList = (List<DistanceStandard>) query
                .executeWithMap(paramMap);
        if (standardList != null && standardList.size() > 0)
            return standardList.get(0);
        else
            return null;

    }

}
