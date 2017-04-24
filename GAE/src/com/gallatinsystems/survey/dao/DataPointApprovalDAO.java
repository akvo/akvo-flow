/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo Flow.
 *
 *  Akvo Flow is free software: you can redistribute it and modify it under the terms of
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

import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.DataPointApproval;

public class DataPointApprovalDAO extends BaseDAO<DataPointApproval> {

    public DataPointApprovalDAO() {
        super(DataPointApproval.class);
    }

    public List<DataPointApproval> listBySurveyedLocaleId(Long surveyedLocaleId) {
        List<DataPointApproval> approvals = this.listByProperty("surveyedLocaleId",
                surveyedLocaleId, "Long");
        if (approvals == null) {
            return Collections.emptyList();
        }
        return approvals;
    }

    public List<DataPointApproval> listBySurveyedLocaleIds(List<Long> surveyedLocaleIds) {
        if (surveyedLocaleIds == null || surveyedLocaleIds.isEmpty()) {
            return Collections.emptyList();
        }
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = ":p2.contains(surveyedLocaleId)";
        javax.jdo.Query query = pm.newQuery(DataPointApproval.class, queryString);
        return (List<DataPointApproval>) query.execute(surveyedLocaleIds);
    }
}
