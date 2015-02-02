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

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class PilotDataFix {
    public static void main(String[] args) {

        final String usr = args[0];
        final String pwd = args[1];

        final RemoteApiOptions options = new RemoteApiOptions().server(
                "instance.appspot.com", 443)
                .credentials(usr, pwd);
        final RemoteApiInstaller installer = new RemoteApiInstaller();

        try {
            installer.install(options);
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

            final Filter f = new FilterPredicate("surveyId", FilterOperator.EQUAL, 20563265L);
            final Query q = new Query("Question").setFilter(f);
            List<Entity> questions = ds.prepare(q).asList(FetchOptions.Builder.withDefaults());

            for (Entity e : questions) {

                Long sourceId = (Long) e.getProperty("sourceId");

                if (sourceId == null) {
                    continue;
                }

                if (!found(questions, sourceId)) {
                    System.out.println("Fixing: " + e.getKey().getId());
                    e.setProperty("sourceId", null);
                    ds.put(e);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }

    }

    private static boolean found(List<Entity> col, Long questionId) {
        for (Entity e : col) {
            if (e.getKey().getId() == questionId) {
                return true;
            }
        }
        return false;
    }

}
