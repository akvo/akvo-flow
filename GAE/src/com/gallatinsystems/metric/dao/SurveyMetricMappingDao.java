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
import com.gallatinsystems.metric.domain.SurveyMetricMapping;

/**
 * persists and retrieves SurveyMetricMapping objects from the datastore
 * 
 * @author Christopher Fagiani
 */
public class SurveyMetricMappingDao extends BaseDAO<SurveyMetricMapping> {

    public SurveyMetricMappingDao() {
        super(SurveyMetricMapping.class);
    }

    /**
     * finds all metric mappings for a given survey and organization
     * 
     * @param surveyId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyMetricMapping> listMappingsBySurvey(Long surveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyMetricMapping.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("surveyId", filterString, paramString, "Long",
                surveyId, paramMap);
        if (surveyId != null) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
            return (List<SurveyMetricMapping>) query.executeWithMap(paramMap);
        } else {
            return list(CURSOR_TYPE.all.toString());
        }
    }

    /**
     * lists all metric mappings for a single survey with the metric id specified
     * 
     * @param metricId
     * @param surveyId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyMetricMapping> listMetricsBySurveyAndMetric(
            Long metricId, Long surveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyMetricMapping.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("surveyId", filterString, paramString, "Long",
                surveyId, paramMap);
        appendNonNullParam("metricId", filterString, paramString, "Long",
                metricId, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        return (List<SurveyMetricMapping>) query.executeWithMap(paramMap);

    }

    /**
     * returns all SurveyMetricMappings for the given questionGroupId
     * 
     * @param questionGroupId
     * @return
     */
    public List<SurveyMetricMapping> listMappingsByQuestionGroup(
            Long questionGroupId) {
        return listByProperty("questionGroupId", questionGroupId, "Long");
    }

    /**
     * lists all mappings for a single question
     * 
     * @param questionId
     * @return
     */
    public List<SurveyMetricMapping> listMappingsByQuestion(Long questionId) {
        return listByProperty("surveyQuestionId", questionId, "Long");
    }

    /**
     * lists all mappings for a single metric
     * 
     * @param metricId
     * @return
     */
    public List<SurveyMetricMapping> listMappingsByMetric(Long metricId) {
        return listByProperty("metricId", metricId, "Long");
    }

    /**
     * deletes all mappings for a given questionGroupId
     * 
     * @param surveyId
     */
    public void deleteMappingsForQuestionGroup(Long questionGroupId) {
        List<SurveyMetricMapping> mappings = listMappingsByQuestionGroup(questionGroupId);
        if (mappings != null && mappings.size() > 0) {
            delete(mappings);
        }
    }

    /**
     * deletes all mappings for a single question
     */
    public void deleteMetricMapping(Long questionId) {
        List<SurveyMetricMapping> mappings = listMappingsByQuestion(questionId);
        if (mappings != null && mappings.size() > 0) {
            delete(mappings);
        }
    }

    /**
     * deletes all mappings for a single metric
     */
    public void deleteMetricMappingByMetric(Long metricId) {
        List<SurveyMetricMapping> mappings = listMappingsByMetric(metricId);
        if (mappings != null && mappings.size() > 0) {
            delete(mappings);
        }
    }

}
