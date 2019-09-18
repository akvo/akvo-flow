/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;

import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.user.domain.User;

public class DataPointAssignmentDao extends BaseDAO<DataPointAssignment> {

    public DataPointAssignmentDao() {
        super(DataPointAssignment.class);
    }

    public List<DataPointAssignment> listBySurvey(Long surveyId) {
        return listByProperty("surveyId", surveyId, "Long");
    }

    public List<DataPointAssignment> listBySurveyAssignment(Long surveyAssignmentId) {
        return listByProperty("surveyAssignmentId", surveyAssignmentId, "Long");
    }

    public List<DataPointAssignment> listByDevice(Long deviceId) {
        return listByProperty("deviceId", deviceId, "Long");
    }

    /**
     * Return a set of data point assignments for a specified Device and survey (UI survey, not Form)
     *
     * @return list of assignments
     */
    public List<DataPointAssignment> listByDeviceAndSurvey(Long deviceId, Long surveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DataPointAssignment.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("deviceId", filterString, paramString, "Long", deviceId, paramMap);
        appendNonNullParam("surveyId", filterString, paramString, "Long", surveyId, paramMap);

        if (filterString.length() > 0) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
        }
        @SuppressWarnings("unchecked")
        List<DataPointAssignment> selected = (List<DataPointAssignment>) query.executeWithMap(paramMap);

        return selected;
    }


}
