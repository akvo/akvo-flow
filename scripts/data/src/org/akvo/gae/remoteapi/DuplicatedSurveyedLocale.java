/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.gae.remoteapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/*
 * - Find duplicated SurveyedLocales
 */
public class DuplicatedSurveyedLocale implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final Query q = new Query("SurveyedLocale").addSort("identifier", SortDirection.ASCENDING);
        final PreparedQuery pq = ds.prepare(q);

        final List<Key> toBeRemoved = new ArrayList<>();

        System.out.println("Processing SurveyedLocales");

        String lastIdentifier = "";
        Long lastId = Long.valueOf(0);
        Date lastCre = null;
        int count = 0;
        
        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long id = (Long) sl.getKey().getId();
            String identifier = (String) sl.getProperty("identifier");
            Date cre = (Date) sl.getProperty("createdDateTime");

            if (identifier.equals(lastIdentifier)) {
                System.err.println(String.format("SurveyedLocale %d created %s identifier %s", lastId, lastCre, identifier));
                System.err.println(String.format("SurveyedLocale %d created %s identifier %s", id, cre, identifier));
//              toBeRemoved.add(sl.getKey());
                count++;
            }

            lastIdentifier = identifier;
            lastId = id;
            lastCre = cre;
        }
        System.out.println("Found " + count);
        if (!toBeRemoved.isEmpty()) {
            System.out.println("Deleting: " + toBeRemoved);
            ds.delete(toBeRemoved);
        }
    }
}
