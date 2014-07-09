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

package com.gallatinsystems.survey.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;

/**
 * dao for manipulating surveyXMLFragments (used in partial publishing).
 * 
 * @author Christopher Fagiani
 */
public class SurveyXMLFragmentDao extends BaseDAO<SurveyXMLFragment> {

    public SurveyXMLFragmentDao() {
        super(SurveyXMLFragment.class);
    }

    /**
     * lists all fragments for a given survey
     * 
     * @param surveyId
     * @param type
     * @param transactionId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SurveyXMLFragment> listSurveyFragments(Long surveyId,
            SurveyXMLFragment.FRAGMENT_TYPE type, Long transactionId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyXMLFragment.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("surveyId", filterString, paramString, "String",
                surveyId.toString(), paramMap);
        appendNonNullParam("transactionId", filterString, paramString, "Long",
                transactionId, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        query.setOrdering("fragmentOrder");
        List<SurveyXMLFragment> results = (List<SurveyXMLFragment>) query
                .execute(surveyId, transactionId);
        return results;
    }

    /**
     * deletes all fragments for the surveyId passed in
     * 
     * @param surveyId
     */
    public void deleteFragmentsForSurvey(Long surveyId, Long transactionId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        List<SurveyXMLFragment> surveyFragmentList = listSurveyFragments(
                surveyId, null, transactionId);
        if (surveyFragmentList != null) {
            pm.deletePersistentAll(surveyFragmentList);
        }
    }
}
