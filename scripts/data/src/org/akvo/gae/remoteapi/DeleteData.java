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

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class DeleteData {

    @SuppressWarnings("unchecked")
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
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

            Filter f = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
            Query q = new Query("QuestionAnswerStore").setFilter(f).setKeysOnly();
            PreparedQuery pq = ds.prepare(q);
            int i = 0;
            for (Entity e : pq.asIterable(FetchOptions.Builder.withChunkSize(100))) {
                System.out.println("Deleting: " + e.getKey().getId());
                ds.delete(e.getKey());
                i++;
            }
            System.out.println("Total QuestionAnswerStore: " + i);

            q = new Query("SurveyInstance").setFilter(f);
            pq = ds.prepare(q);

            new ArrayList<Long>();
            int j = 0;
            int k = 0;

            for (Entity si : pq.asIterable(FetchOptions.Builder.withChunkSize(100))) {
                Long slId = null;
                try {
                    slId = (Long) si.getProperty("surveyedLocaleId");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                if (slId != null) {
                    Filter slf = new FilterPredicate("__key__", FilterOperator.EQUAL,
                            KeyFactory.createKey(
                                    "SurveyedLocale", slId));
                    Query slq = new Query("SurveyedLocale").setFilter(slf);

                    Entity sl = ds.prepare(slq).asSingleEntity();

                    List<Long> contrib = (List<Long>) sl.getProperty("surveyInstanceContrib");

                    if (contrib != null && contrib.size() == 1
                            && contrib.contains(si.getKey().getId())) {
                        System.out.println("Deleting SurveyedLocale: " + sl.getKey().getId());
                        ds.delete(sl.getKey());
                        k++;
                    }

                }
                System.out.println("Deleting SurveyInstance: " + si.getKey().getId());
                ds.delete(si.getKey());
                j++;
            }
            System.out.println("Total SurveyInstances: " + j);
            System.out.println("Total SurveyedLocales: " + k);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            installer.uninstall();
        }
    }
}
