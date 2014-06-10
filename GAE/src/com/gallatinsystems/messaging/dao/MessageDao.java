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

package com.gallatinsystems.messaging.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.messaging.domain.Message;

/**
 * Data access object for manipulating Message objects
 * 
 * @author Christopher Fagiani
 */
public class MessageDao extends BaseDAO<Message> {

    public MessageDao() {
        super(Message.class);
    }

    /**
     * lists all messages
     * 
     * @param about - optional subject
     * @param id - optional ID
     * @param cursor - cursor string
     * @return - all messages matching criteria
     */
    @SuppressWarnings("unchecked")
    public List<Message> listBySubject(String about, Long id, String cursor) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Message.class);
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        Map<String, Object> paramMap = null;
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("actionAbout", filterString, paramString, "String",
                about, paramMap);
        appendNonNullParam("objectId", filterString, paramString, "Long", id,
                paramMap);

        if (filterString.toString().trim().length() > 0) {
            query.setFilter(filterString.toString());
            query.declareParameters(paramString.toString());
        }
        query.setOrdering("lastUpdateDateTime desc");
        prepareCursor(cursor, query);
        List<Message> results = (List<Message>) query.executeWithMap(paramMap);
        return results;
    }

}
