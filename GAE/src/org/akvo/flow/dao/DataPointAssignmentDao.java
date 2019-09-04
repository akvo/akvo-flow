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

    /**
     * Return a set of data point ids for a specified device and survey (in any assignment)
     *
     * @return list of assignments
     */
    public Set<Long> listDataPointIds(Long deviceId, Long surveyId) {
        SurveyAssignmentDao saDao = new SurveyAssignmentDao();
        Set<Long> result = new HashSet<>();
        List<SurveyAssignment> assignments = saDao.listBySurveyGroup(surveyId);
        for (SurveyAssignment sa: assignments) {
            PersistenceManager pm = PersistenceFilter.getManager();
            javax.jdo.Query query = pm.newQuery(User.class);
            StringBuilder filterString = new StringBuilder();
            StringBuilder paramString = new StringBuilder();
            Map<String, Object> paramMap = null;
            paramMap = new HashMap<String, Object>();
            ///TODO: add this index
            appendNonNullParam("deviceId", filterString, paramString, "Long", deviceId, paramMap);
            appendNonNullParam("surveyAssignmentId", filterString, paramString, "Long", sa.getKey().getId(), paramMap);

            if (filterString.length() > 0) {
                query.setFilter(filterString.toString());
                query.declareParameters(paramString.toString());
            }
            //TODO mandatory? Or do we want pagination?: prepareCursor(cursorString, query);
            @SuppressWarnings("unchecked")
            List<DataPointAssignment> selected = (List<DataPointAssignment>) query.executeWithMap(paramMap);

            for (DataPointAssignment dpa:selected) {
                result.addAll(dpa.getDataPointIds());
            }
        }
        return result;
    }
}
