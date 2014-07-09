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

package com.gallatinsystems.metric.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.metric.domain.Metric;

/**
 * Data access object for persisting Metric objects.
 * 
 * @author Christopher Fagiani
 */
public class MetricDao extends BaseDAO<Metric> {

    public MetricDao() {
        super(Metric.class);
    }

    /**
     * lists all metrics for a given organization, optionally filtered by group.
     * 
     * @param organization
     * @param group
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Metric> listMetrics(String name, String group, String valueType,
            String organization, String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Metric.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("organization", filterString, paramString, "String",
                organization, paramMap);
        appendNonNullParam("group", filterString, paramString, "String", group,
                paramMap);
        appendNonNullParam("name", filterString, paramString, "String", name,
                paramMap);
        appendNonNullParam("valueType", filterString, paramString, "String", valueType,
                paramMap);
        query.setOrdering("name asc");
        if (filterString.toString().trim().length() > 0) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
            prepareCursor(cursorString, query);
            return (List<Metric>) query.executeWithMap(paramMap);
        } else {
            prepareCursor(cursorString, query);
            return (List<Metric>) query.execute();
        }
    }

}
