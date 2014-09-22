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

package org.waterforpeople.mapping.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.domain.AccessPointMetricMapping;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * persists/finds AccessPointMetricMappings
 * 
 * @author Christopher Fagiani
 */
public class AccessPointMetricMappingDao extends
        BaseDAO<AccessPointMetricMapping> {

    public AccessPointMetricMappingDao() {
        super(AccessPointMetricMapping.class);
    }

    /**
     * finds mappings based on the non-null parameters passed in. At least 1 must be non-null.
     * 
     * @param org
     * @param name
     * @param group
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AccessPointMetricMapping> findMappings(String org,
            String group, String name) {
        if (org != null || group != null || name != null) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            StringBuilder filterString = new StringBuilder();
            StringBuilder paramString = new StringBuilder();

            appendNonNullParam("organization", filterString, paramString,
                    "String", org, paramMap);
            appendNonNullParam("metricName", filterString, paramString,
                    "String", name, paramMap);
            appendNonNullParam("metricGroup", filterString, paramString,
                    "String", group, paramMap);

            PersistenceManager pm = PersistenceFilter.getManager();
            javax.jdo.Query query = pm.newQuery(AccessPointMetricSummary.class);
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());

            return (List<AccessPointMetricMapping>) query
                    .executeWithMap(paramMap);
        } else {
            return list(CURSOR_TYPE.all.toString());
        }
    }

}
