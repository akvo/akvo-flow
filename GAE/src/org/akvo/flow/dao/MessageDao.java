/*
 *  Copyright (C) 2010-2012, 2019 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.akvo.flow.domain.Message;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * Data access object for manipulating Message objects
 *
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

    /**
     * lists keys of messages older than a specific date
     */
    public List<Key> listKeysCreatedBefore(Date beforeDate) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // The Query interface assembles a query
        com.google.appengine.api.datastore.Query q =
                new com.google.appengine.api.datastore.Query("Message");
        q.setKeysOnly();
        q.setFilter(new Query.FilterPredicate(
                "createdDateTime", FilterOperator.LESS_THAN_OR_EQUAL, beforeDate));
        PreparedQuery pq = datastore.prepare(q);
        List<Key> result = new ArrayList<>();
        for (Entity e: pq.asIterable()) {
            result.add(e.getKey());
        }
        return result;
    }


}
