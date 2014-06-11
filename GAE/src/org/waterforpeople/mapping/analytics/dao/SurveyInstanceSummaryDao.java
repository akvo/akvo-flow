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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * logic for saving/finding SurveyInstanceSummary objects
 * 
 * @author Christopher Fagiani
 */
public class SurveyInstanceSummaryDao extends BaseDAO<SurveyInstanceSummary> {

    public SurveyInstanceSummaryDao() {
        super(SurveyInstanceSummary.class);
    }

    /**
     * synchronized static method so that only 1 thread can be updating a summary at a time. This is
     * inefficient but is the only way we can be sure we're keeping the count consistent since there
     * is no "select for update" or sql dml-like construct
     * 
     * @param answer
     */
    @SuppressWarnings("rawtypes")
    public static synchronized void incrementCount(String community,
            String country, Date collectionDate, int delta) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyInstanceSummary.class);
        Date colDate = DateUtil.getDateNoTime(collectionDate);
        query
                .setFilter("countryCode == countryCodeParam && communityCode == communityCodeParam && collectionDate == collectionDateParam");
        query
                .declareParameters("String countryCodeParam, String communityCodeParam, Date collectionDateParam");
        // have to import the date class before we can use it
        query.declareImports("import java.util.Date");
        List results = (List) query.execute(country, community, colDate);

        SurveyInstanceSummary summary = null;
        SurveyInstanceSummaryDao thisDao = new SurveyInstanceSummaryDao();
        if (results == null || results.size() == 0) {
            summary = new SurveyInstanceSummary();
            summary.setCount(1L);
            summary.setCommunityCode(community);
            summary.setCountryCode(country);
            summary.setCollectionDate(colDate);
            thisDao.save(summary);
        } else {
            summary = (SurveyInstanceSummary) results.get(0);
            summary.setCount(summary.getCount() + delta);
            // if the count is zero, delete it
            if (summary.getCount() == 0) {
                thisDao.delete(summary);
            } else {
                thisDao.save(summary);
            }
        }
    }

    /**
     * Lists all summary objects matching the country and/or community passed in. If both are null,
     * all results are returned
     * 
     * @param countryCode
     * @param communityCode
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyInstanceSummary> listByLocation(String countryCode,
            String communityCode) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyInstanceSummary.class);
        List<SurveyInstanceSummary> results = null;
        if (countryCode != null || communityCode != null) {
            StringBuilder filter = new StringBuilder();
            StringBuilder param = new StringBuilder();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            this.appendNonNullParam("countryCode", filter, param, "String",
                    countryCode, paramMap);
            this.appendNonNullParam("communityCode", filter, param, "String",
                    communityCode, paramMap);
            query.setFilter(filter.toString());
            query.declareParameters(param.toString());
            results = (List<SurveyInstanceSummary>) query
                    .executeWithMap(paramMap);
        } else {
            results = list(Constants.ALL_RESULTS);
        }
        return results;
    }

    /**
     * Returns a surveyInstanceSummary based on surveyId
     * 
     * @param uuid
     * @return
     */
    public SurveyInstanceSummary findBySurveyId(Long surveyId) {
        return findByProperty("surveyId", surveyId, "Long");
    }

}
