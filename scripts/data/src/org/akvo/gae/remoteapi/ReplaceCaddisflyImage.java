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
import com.google.appengine.api.datastore.Text;

import java.util.ArrayList;
import java.util.List;

/*
 Search for responses to a specific question and add an argument to delete all the responses
 for only that question
 */
public class ReplaceCaddisflyImage implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Missing question Id");
            System.exit(1);
        }


        final Long questionId = Long.parseLong(args[0].trim());

        try {
            Entity question = ds.get(KeyFactory.createKey("Question", questionId));
            List<Object> responseIds = getCaddisflyResponses(ds, questionId.toString());
        } catch (EntityNotFoundException e) {
            System.out.println("Question not found: " + questionId);
            return;
        }
    }

    private String replaceImage(String jsonString) {
        return jsonString.replaceAll("([:\"^[0-9a-zA-Z]]+[:\\-^[0-9a-zA-Z]{4}]+[:\\-^[0-9a-zA-Z]{4}]+[:\\-^[0-9a-zA-Z]{4}]+[:\\-^[0-9a-zA-Z]{4}]+[:\\-^[0-9a-zA-Z]{12}]+[:^.]+(?:jpg|png)+)", "\"image\":\"new-image.jpg\"");
    }


    private List<Object> getCaddisflyResponses(DatastoreService ds, String questionId) {
        final Filter f = new FilterPredicate("questionID", FilterOperator.EQUAL, questionId);
        final Query q = new Query("QuestionAnswerStore").setFilter(f);
        final PreparedQuery pq = ds.prepare(q);
        final List<Object> values = new ArrayList<>();
        for (Entity e : pq.asList(FetchOptions.Builder.withDefaults())) {
            Text valueText = (Text) e.getProperty("valueText");
            Object value = valueText.getValue();
            System.out.println(replaceImage(value.toString()));
            System.out.println(value);
            values.add(value);
        }
        System.out.println(String.format("Found %s responses...", values.size()));
        return values;
    }
}
