/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class DeleteQuestionWithoutQuestionGroup implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final Query q = new Query("Question").addSort("createdDateTime", Query.SortDirection.DESCENDING);
        final PreparedQuery pq = ds.prepare(q);
        final FetchOptions fetchOptions = FetchOptions.Builder.withDefaults().prefetchSize(1000).chunkSize(1000);


        long i = 0;
        final Set<Key> candidates = new HashSet<>();
        for (Entity e : pq.asIterable(fetchOptions)) {
            if (e.getProperty("questionGroupId") == null) {
                candidates.add(e.getKey());
            }
            i++;
            if (i % 100 == 0) {
                System.out.print(".");
            }
            if (i % 1000 == 0) {
                System.out.println();
            }
        }

        Set<Key> toDelete = new HashSet<>(candidates);
        for (Key k : candidates) {
            final Query qData = new Query("QuestionAnswerStore");
            final Query.FilterPredicate filter = new Query.FilterPredicate("questionID", Query.FilterOperator.EQUAL, String.valueOf(k.getId()));
            qData.setFilter(filter);
            final PreparedQuery pqData = ds.prepare(qData);
            final List<Entity> data = pqData.asList(FetchOptions.Builder.withDefaults().limit(10).chunkSize(10).prefetchSize(10));
            if (!data.isEmpty()) {
                toDelete.remove(k);
            }
        }

        System.out.println();
        System.out.println("Total processed: " + i);
        System.out.println("Candidates [" + candidates.size() + "]: " + candidates);
        System.out.println("With no data [" + toDelete.size() + "]: " + toDelete);

        if (toDelete.isEmpty()) {
            System.out.println("Nothing to delete");
            return;
        }

        System.out.print("Delete? YES/no ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.next();

        if ("YES".equals(answer)) {
            System.out.println("Deleting...");
            ds.delete(toDelete);
        }
    }
}
