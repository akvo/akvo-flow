/*
 *  Copyright (C) 2014-2015 Stichting Akvo (Akvo Foundation)
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
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class DeleteData {

    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println(DeleteData.class.getName()
                    + " <appid> <username> <password> <surveyId>");
            System.exit(1);
        }

        final String appid = args[0];
        final String usr = args[1];
        final String pwd = args[2];
        final Long surveyId = Long.parseLong(args[3]);

        final RemoteApiOptions options = new RemoteApiOptions().server(
                appid + ".appspot.com", 443)
                .credentials(usr, pwd);
        final RemoteApiInstaller installer = new RemoteApiInstaller();

        try {
            installer.install(options);

            final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            final List<String> kinds = Arrays.asList("SurveyalValue", "QuestionAnswerStore",
                    "SurveyInstance");

            for (String kind : kinds) {
                deleteEntities(ds, kind, surveyId);
            }

            deleteSurveyedLocale(ds, surveyId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
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
    private static void deleteSurveyedLocale(DatastoreService ds, Long surveyId) {
        // NOTE: It's not possible to query by the `surveyInstanceContrib` property
        final Query q = new Query("SurveyedLocale");
        final PreparedQuery pq = ds.prepare(q);
        final List<Key> keys = new ArrayList<Key>();

        for (Entity e : pq.asList(FetchOptions.Builder.withChunkSize(500))) {
            Collection<Long> contrib = (Collection<Long>) e.getProperty("surveyInstanceContrib");
            if (contrib.size() == 1 && contrib.contains(surveyId)) {
                keys.add(e.getKey());
            }
        }
        System.out.println(String.format("SurveyedLocale - deleting %s enties - surveyId = %s",
                keys.size(), surveyId));
        ds.delete(keys);
    }
}
