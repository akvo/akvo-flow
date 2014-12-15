/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class DeleteSurveyInstances {

    public static void main(String[] args) {

        final String usr = args[0];
        final String pwd = args[1];
        final Long surveyId = Long.parseLong(args[2]);

        final RemoteApiOptions options = new RemoteApiOptions().server(
                "instance.appspot.com", 443)
                .credentials(usr, pwd);
        final RemoteApiInstaller installer = new RemoteApiInstaller();

        try {

            installer.install(options);
            final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

            final Filter f = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
            final Query q = new Query("SurveyInstance").setFilter(f).setKeysOnly();
            final PreparedQuery pq = ds.prepare(q);
            for (final Entity e : pq.asIterable(FetchOptions.Builder.withChunkSize(100))) {
                final Filter qasF = new FilterPredicate("surveyInstanceId", FilterOperator.EQUAL, e
                        .getKey().getId());
                final Query qasQ = new Query("QuestionAnswerStore").setFilter(qasF).setKeysOnly();
                if (ds.prepare(qasQ).asList(FetchOptions.Builder.withDefaults()).size() == 0) {
                    ds.delete(e.getKey());
                    System.out.println("Deleted " + e.getKey().getId());
                }
            }
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }
    }
}
