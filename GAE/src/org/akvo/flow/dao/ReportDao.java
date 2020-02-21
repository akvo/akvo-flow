/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;

import org.akvo.flow.domain.Message;
import org.akvo.flow.domain.persistent.Report;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

public class ReportDao extends BaseDAO<Report> {

    public ReportDao() {
        super(Report.class);
    }

    /**
     * lists all reports with a given user and optionally type
     */
    @SuppressWarnings("unchecked")
    public List<Report> listByUserAndType(long user, @Nullable String reportType) {
        if (reportType == null) {
            return listByProperty("user", user, "Long", Report.class);
        }

        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Message.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("user", filterString, paramString, "Long", user, paramMap);
        appendNonNullParam("reportType", filterString, paramString, "String", reportType, paramMap);

        if (filterString.toString().trim().length() > 0) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
        }
        return (List<Report>) query.executeWithMap(paramMap);
    }

    /**
     * lists all reports by the current user
     */
    public List<Report> listAllByCurrentUserAndType(@Nullable String reportType) {
        return listByUserAndType(currentUserId(), reportType);
    }

    /**
     * lists all reports older than a specific date
     */
    public List<Report> listAllCreatedBefore(Date date) {
        return listByProperty("createdDateTime", date, "Date",
                "createdDateTime", null, LTE_OP, Report.class);
    }

    //prevent crashes if user info unavailable
    public long currentUserId() {
        SecurityContext c = SecurityContextHolder.getContext();
        if (c == null) return 0;
        Authentication a = c.getAuthentication();
        if (a == null) return 0;
        if (a.getCredentials() instanceof Long)return (Long)a.getCredentials();
        return 0;
    }


}
