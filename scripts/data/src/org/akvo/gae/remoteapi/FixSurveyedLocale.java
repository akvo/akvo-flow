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

/*
 * - Adds surveyGroupId property to SurveyedLocale when missing
 * - Removes SurveyedLocale that are linked to a non existing Survey
 */
public class FixSurveyedLocale implements Process {

    private static String ERR_MSG = "Unable to fix SurveyedLocale [%s], reason: %s";

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final Filter f = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, null);
        final Query q = new Query("SurveyedLocale").setFilter(f);
        final PreparedQuery pq = ds.prepare(q);

        final List<Key> toBeRemoved = new ArrayList<>();

        System.out.println("Processing SurveyedLocales");

        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyId = (Long) sl.getProperty("creationSurveyId");

            if (surveyId == null) {

                final Long surveyInstanceId = (Long) sl.getProperty("lastSurveyalInstanceId");

                if (surveyInstanceId == null) {
                    System.err.println(String.format(ERR_MSG, sl.getKey().getId(),
                            "missing property creationSurveyId and lastSurveyalInstanceId"));
                    continue;
                }

                // trying to get surveyGroupId from SurveyInstance -> Survey

                final Filter fsi = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                        FilterOperator.EQUAL, KeyFactory.createKey("SurveyInstance",
                                surveyInstanceId));
                final Query qsi = new Query("SurveyInstance").setFilter(fsi);
                final Entity si = ds.prepare(qsi).asSingleEntity();

                if (si == null) {
                    System.err.println(String.format(ERR_MSG, sl.getKey().getId(),
                            "missing property creationSurveyId and lastSurveyalInstanceId"));
                    continue;
                }

                surveyId = (Long) si.getProperty("surveyId");

                if (surveyId == null) {
                    System.err.println(String.format(ERR_MSG, sl.getKey().getId(),
                            "SurveyInstance [" + si.getKey().getId()
                                    + "] does not have surveyId property"));
                    continue;
                }
            }

            final Filter fs = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                    FilterOperator.EQUAL, KeyFactory.createKey("Survey", surveyId));
            final Query qs = new Query("Survey").setFilter(fs);
            final Entity survey = ds.prepare(qs).asSingleEntity();

            if (survey == null) {
                System.err.println(String.format(ERR_MSG, sl.getKey().getId(), "Survey ["
                        + surveyId + "] not found"));
                System.err.println("Marking SurveyedLocale [" + sl.getKey().getId()
                        + "] to be deleted");
                toBeRemoved.add(sl.getKey());
                continue;
            }

            Long surveyGroupId = (Long) survey.getProperty("surveyGroupId");

            if (surveyGroupId == null) {
                System.err.println(String.format(ERR_MSG, sl.getKey().getId(),
                        "surveyGroupId is null for Survey [" + surveyId + "]"));
                continue;
            }

            sl.setProperty("surveyGroupId", surveyGroupId);
            ds.put(sl);
        }

        if (!toBeRemoved.isEmpty()) {
            System.out.println("Deleting: " + toBeRemoved);
            ds.delete(toBeRemoved);
        }
    }
}
