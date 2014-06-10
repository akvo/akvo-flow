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

package com.gallatinsystems.diagnostics.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

/**
 * Persists and retrieves remoteStacktrace objects from the data store
 * 
 * @author Christopher Fagiani
 */
public class RemoteStacktraceDao extends BaseDAO<RemoteStacktrace> {

    public RemoteStacktraceDao() {
        super(RemoteStacktrace.class);
    }

    /**
     * lists all stacktrace objects in the database. If unAckOnly is true, only unacknowledged
     * exceptions will be returned.
     * 
     * @param phoneNumber
     * @param deviceId
     * @param unAckOnly
     * @param cursorString
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<RemoteStacktrace> listStacktrace(String phoneNumber,
            String deviceId, boolean unAckOnly, String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        List<RemoteStacktrace> results = null;
        javax.jdo.Query q = pm.newQuery(RemoteStacktrace.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        appendNonNullParam("phoneNumber", filterString, paramString, "String",
                phoneNumber, paramMap);
        appendNonNullParam("deviceIdentifier", filterString, paramString,
                "String", deviceId, paramMap);
        if (unAckOnly) {
            appendNonNullParam("acknowleged", filterString, paramString,
                    "java.lang.Boolean", false, paramMap);
        }
        q.setOrdering("errorDate desc");
        if (unAckOnly || phoneNumber != null || deviceId != null) {
            q.setFilter(filterString.toString());
            q.declareParameters(paramString.toString());
            prepareCursor(cursorString, q);
            results = (List<RemoteStacktrace>) q.executeWithMap(paramMap);
        } else {
            prepareCursor(cursorString, q);
            results = (List<RemoteStacktrace>) q.execute();
        }
        return results;
    }

    /**
     * deletes all items older than the date passed in
     * 
     * @param date
     */
    public long deleteItemsOlderThan(Date date) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(RemoteStacktrace.class);
        query.setFilter("errorDate < dateParam");
        query.declareParameters("Date dateParam");
        query.declareImports("import java.util.Date");
        return query.deletePersistentAll(date);
    }

}
