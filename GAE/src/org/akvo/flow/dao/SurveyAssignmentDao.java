/*
 *  Copyright (C) 2012, 2019 Stichting Akvo (Akvo Foundation)
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
import java.util.List;
import java.util.Map;

import com.gallatinsystems.framework.servlet.PersistenceFilter;
import org.akvo.flow.domain.persistent.DataPointAssignment;
import org.akvo.flow.domain.persistent.SurveyAssignment;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

import javax.jdo.PersistenceManager;

public class SurveyAssignmentDao extends BaseDAO<SurveyAssignment> {

    public SurveyAssignmentDao() {
        super(SurveyAssignment.class);
    }

    /**
     * Return a list of survey assignments containing a specified device
     *
     * @return list of assignments
     */
    public List<SurveyAssignment> listAllContainingDevice(Long devId) {
        List<SurveyAssignment> selected = listByProperty("deviceIds", devId, "Long");
        return selected;
    }

    /**
     * Remove a device from all assignments
     *
     * @return number of affected assignments
     */
    public int removeDevice(Long devId) {
        List<SurveyAssignment> all = listAllContainingDevice(devId);
        for (SurveyAssignment ass : all) {
            List<Long> devs = ass.getDeviceIds();
            devs.remove(devId);
        }
        save(all);
        return all.size();
    }

    public List<SurveyAssignment> listByDeviceAndSurvey(Long deviceId, Long surveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(SurveyAssignment.class);
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

        return (List<SurveyAssignment>) query.executeWithMap(paramMap);
    }


}
