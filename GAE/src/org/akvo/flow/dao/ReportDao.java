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

import java.util.Collections;
import java.util.List;

import org.akvo.flow.domain.persistent.Report;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.framework.dao.BaseDAO;

public class ReportDao extends BaseDAO<Report> {

    public ReportDao() {
        super(Report.class);
    }

    /**
     * lists all reports with a given user
     */
    public List<Report> listByUser(Long user) {
        return listByProperty("user", user, "Long",
                Report.class);
    }

    /**
     * lists all reports by the current user
     */
    public List<Report> listAllByCurrentUser() {
        final Object credentials = SecurityContextHolder.getContext()
                .getAuthentication().getCredentials();
        if (credentials instanceof Long) {
            return listByUser((Long) credentials);
        } else {
            return Collections.emptyList();
        }

    }





}
