/*
 *  Copyright (C) 2022 Stichting Akvo (Akvo Foundation)
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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.ArrayList;
import java.util.List;

/*
 Search for responses to a specific question and add an argument to delete all the responses
 for only that question
 */
public class FindQuestionResponses implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Missing question Id");
            System.exit(1);
        }


        final Long questionId = Long.parseLong(args[0].trim());
        boolean delete = false;
        if (args.length == 2) {
            delete = args[1].trim().equals("--delete");
        }

        try {
            Entity question = ds.get(KeyFactory.createKey("Question", questionId));
            List<Key> responseIds = findQuestionResponses(ds, delete, questionId.toString());
            if (delete) {
                System.out.println(String.format("Deleting %s responses...", responseIds.size()));
                DataUtils.batchDelete(ds, responseIds);
            }
        } catch (EntityNotFoundException e) {
            System.out.println("Question not found: " + questionId);
            return;
        }
    }

    private List<Key> findQuestionResponses(DatastoreService ds, boolean delete, String questionId) {
        final Filter f = new FilterPredicate("questionID", FilterOperator.EQUAL, questionId);
        final Query q = new Query("QuestionAnswerStore").setFilter(f).setKeysOnly();
        final PreparedQuery pq = ds.prepare(q);
        final List<Key> keys = new ArrayList<Key>();
        for (Entity e : pq.asList(FetchOptions.Builder.withDefaults())) {
            keys.add(e.getKey());
        }
        System.out.println(String.format("Found %s responses...", keys.size()));
        return keys;
    }
}
