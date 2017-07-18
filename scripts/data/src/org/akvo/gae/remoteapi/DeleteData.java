/*
 *  Copyright (C) 2014-2015,2017 Stichting Akvo (Akvo Foundation)
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
import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class DeleteData implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final Long surveyId = Long.parseLong(args[0]);

        final List<String> kinds = Arrays.asList("SurveyalValue", "QuestionAnswerStore",
                "SurveyInstance");

        for (String kind : kinds) {
            deleteEntities(ds, kind, surveyId);
        }

        Entity survey = ds.get(KeyFactory.createKey("Survey", surveyId));
        Long surveyGroupId = (Long) survey.getProperty("surveyGroupId");
        if (surveyGroupId != null) {
            deleteSurveyedLocale(ds, surveyGroupId);
        }
    }

    private static void deleteEntities(DatastoreService ds, String kind, Long surveyId) {
        final Filter f = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
        final Query q = new Query(kind).setFilter(f).setKeysOnly();
        final PreparedQuery pq = ds.prepare(q);
        final List<Key> keys = new ArrayList<Key>();
        for (Entity e : pq.asList(FetchOptions.Builder.withChunkSize(500))) {
            keys.add(e.getKey());
        }
        System.out.println(String.format("%s - deleting %s enties - surveyId = %s", kind,
                keys.size(), surveyId));
        ds.delete(keys);
    }

    @SuppressWarnings("unchecked")
    private static void deleteSurveyedLocale(DatastoreService ds, Long surveyGroupId) {
        final Filter f = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, surveyGroupId);
        final Query q = new Query("SurveyedLocale").setFilter(f).setKeysOnly();
        final PreparedQuery pq = ds.prepare(q);
        final List<Key> keys = new ArrayList<Key>();

        for (Entity e : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            keys.add(e.getKey());
        }
        System.out.println(String.format(
                "SurveyedLocale - deleting %s enties - surveyGroupId = %s",
                keys.size(), surveyGroupId));
        ds.delete(keys);
    }

}
