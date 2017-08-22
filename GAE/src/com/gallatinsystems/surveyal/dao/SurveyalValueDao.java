/*  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.surveyal.domain.SurveyalValue;

/**
 * Data access object for manipulating SurveyalValues
 */
public class SurveyalValueDao extends BaseDAO<SurveyalValue> {

    public SurveyalValueDao() {
        super(SurveyalValue.class);
    }

    /**
     * lists all surveyalValues for a certain surveyId
     *
     * @param cursor
     * @param pagesize
     * @param surveyId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyalValue> listBySurvey(Long surveyId, String cursor, Integer pageSize) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyalValue.class);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        appendNonNullParam("surveyId", filterString, paramString, "Long",
                surveyId, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        prepareCursor(cursor, pageSize, query);
        List<SurveyalValue> results = (List<SurveyalValue>) query
                .executeWithMap(paramMap);
        return results;
    }

    /**
     * List the surveyal values associated with a specific question
     *
     * @param questionId
     * @return
     */
    public List<SurveyalValue> listByQuestion(Long questionId) {
        return listByProperty("surveyQuestionId", questionId, "Long");
    }
}
