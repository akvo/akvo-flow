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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.map.dao.OGRFeatureDao;
import com.gallatinsystems.gis.map.domain.OGRFeature;

/**
 * Dao for manipulating access point summary domain objects
 * 
 * @author Christopher Fagiani
 */
public class AccessPointMetricSummaryDao extends
        BaseDAO<AccessPointMetricSummary> {
    private static final int NUM_SHARDS = 11;

    public AccessPointMetricSummaryDao() {
        super(AccessPointMetricSummary.class);
    }

    /**
     * lists metrics that match the prototype passed in. The object passed in must have at least 1
     * field populated (besides count). In practice, callers should populate as many fields as
     * possible to narrow results. This will collapse any shards and objects returned will not have
     * any keys (since they are transient roll-up objects)
     * 
     * @param prototype
     * @return
     */
    public List<AccessPointMetricSummary> listMetrics(
            AccessPointMetricSummary prototype) {
        return listMetrics(prototype, true);
    }

    /**
     * lists metrics that match the prototype passed in. The object passed in must have at least 1
     * field populated (besides count). In practice, callers should populate as many fields as
     * possible to narrow results. This will collapse any shards and objects returned will not have
     * any keys (since they are transient roll-up objects) if fetchCentroid is true, the summaries
     * returned will have their centroid lat/lon popuated using the OGRFeature that corresponds to
     * the sublevel. Setting this to true will degrade performance when listing a large number of
     * summaries.
     * 
     * @param prototype
     * @param fetchCentroid
     * @return
     */
    public List<AccessPointMetricSummary> listMetrics(
            AccessPointMetricSummary prototype, boolean fetchCentroid) {
        List<AccessPointMetricSummary> summaries = listMetrics(prototype, null);
        Map<String, AccessPointMetricSummary> rollups = new HashMap<String, AccessPointMetricSummary>();
        if (summaries != null) {
            for (AccessPointMetricSummary s : summaries) {
                AccessPointMetricSummary rollup = rollups.get(s
                        .identifierString());
                if (rollup == null) {
                    rollup = new AccessPointMetricSummary();
                    rollup.setCount((s.getCount() != null ? s.getCount() : 0));
                    rollup.setCountry(s.getCountry());
                    rollup.setLastUpdateDateTime(s.getLastUpdateDateTime());
                    rollup.setMetricGroup(s.getMetricGroup());
                    rollup.setMetricName(s.getMetricName());
                    rollup.setMetricValue(s.getMetricValue());
                    rollup.setOrganization(s.getOrganization());
                    rollup.setSubLevel(s.getSubLevel());
                    rollup.setSubLevelName(s.getSubLevelName());
                    rollup.setSubValue(s.getSubValue());
                    rollup.setPeriodType(s.getPeriodType());
                    rollup.setPeriodValue(s.getPeriodValue());
                    rollups.put(s.identifierString(), rollup);
                    rollup.setParentSubName(s.getParentSubName());
                } else {
                    rollup.setCount(rollup.getCount()
                            + (s.getCount() != null ? s.getCount() : 0));
                }
            }
        }
        List<AccessPointMetricSummary> rollupList = new ArrayList<AccessPointMetricSummary>();
        rollupList.addAll(rollups.values());
        if (fetchCentroid) {
            // TODO: this shouldn't be in the Dao. Ask dru why he needs this
            OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();

            for (AccessPointMetricSummary item : rollupList) {
                if (item.getSubValue() != null) {
                    List<OGRFeature> ogr = ogrFeatureDao
                            .listBySubLevelCountryName(item.getCountry(),
                                    prototype.getSubLevel(),
                                    item.getSubValue(), "all",
                                    item.getParentSubName());
                    for (OGRFeature ogrItem : ogr) {
                        item.setLatitude(ogrItem.getCentroidLat());
                        item.setLongitude(ogrItem.getCentroidLon());
                    }
                }
            }
        }
        return rollupList;
    }

    /**
     * gets the metric matching the prototype passed in with a specific shard number
     * 
     * @param prototype
     * @param shardNum
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AccessPointMetricSummary> listMetrics(
            AccessPointMetricSummary prototype, Integer shardNum) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();

        appendNonNullParam("organization", filterString, paramString, "String",
                prototype.getOrganization(), paramMap);
        appendNonNullParam("country", filterString, paramString, "String",
                prototype.getCountry(), paramMap);
        appendNonNullParam("subLevel", filterString, paramString, "Integer",
                prototype.getSubLevel(), paramMap);
        appendNonNullParam("subValue", filterString, paramString, "String",
                prototype.getSubValue(), paramMap);
        appendNonNullParam("metricName", filterString, paramString, "String",
                prototype.getMetricName(), paramMap);
        appendNonNullParam("metricGroup", filterString, paramString, "String",
                prototype.getMetricGroup(), paramMap);
        appendNonNullParam("metricValue", filterString, paramString, "String",
                prototype.getMetricValue(), paramMap);
        appendNonNullParam("shardNum", filterString, paramString, "Integer",
                shardNum, paramMap);

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(AccessPointMetricSummary.class);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        return (List<AccessPointMetricSummary>) query.executeWithMap(paramMap);

    }

    /**
     * synchronized static method so that only 1 thread can be updating a summary at a time. This is
     * inefficient but is the only way we can be sure we're keeping the count consistent since there
     * is no "select for update" or sql dml-like construct. When this method is called, the metric
     * object passed in must have its value populated as well as at LEAST the organization and
     * country.
     * 
     * @param answer
     */
    public static synchronized void incrementCount(
            AccessPointMetricSummary metric, int unit) {
        AccessPointMetricSummaryDao dao = new AccessPointMetricSummaryDao();
        Random generator = new Random();
        int shardNum = generator.nextInt(NUM_SHARDS);
        List<AccessPointMetricSummary> results = dao.listMetrics(metric,
                shardNum);
        AccessPointMetricSummary summary = null;
        if ((results == null || results.size() == 0) && unit > 0) {
            metric.setCount(new Long(unit));
            metric.setShardNum(shardNum);
            summary = metric;
        } else if (results != null && results.size() > 0) {
            summary = (AccessPointMetricSummary) results.get(0);
            summary.setCount(summary.getCount() + unit);
        }
        if (summary != null) {
            if (summary.getCount() > 0) {
                dao.save(summary);
            } else if (summary.getKey() != null) {
                // if count has been decremented to 0 and the object is
                // already persisted, delete it
                dao.delete(summary);
            }
        }
    }
}
