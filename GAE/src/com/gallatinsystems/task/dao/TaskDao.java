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

package com.gallatinsystems.task.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.task.domain.Task;
import com.gallatinsystems.task.domain.Task.TaskStatus;

/**
 * stores and retrievs tasks from the data store.
 * 
 * @author Christopher Fagiani
 */
public class TaskDao extends BaseDAO<Task> {

    public TaskDao() {
        super(Task.class);
    }

    /**
     * lists tasks based on the countyr code and the status passed in. If parameters are null, then
     * all tasks are returned.
     * 
     * @param countryCode
     * @param status
     * @param cursorString
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Task> listTasksByCountry(String countryCode, TaskStatus status,
            String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Task.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("countryCode", filterString, paramString, "String",
                countryCode, paramMap);
        appendNonNullParam("status", filterString, paramString, "String",
                status, paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        prepareCursor(cursorString, query);

        return (List<Task>) query.executeWithMap(paramMap);
    }

}
