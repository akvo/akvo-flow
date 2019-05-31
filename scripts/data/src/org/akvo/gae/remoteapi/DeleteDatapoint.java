/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Delete SurveyedLocale with dependent items, except SurveyalValue, which is being retired elsewhere
 */
public class DeleteDatapoint implements Process {

    boolean doDelete = false;
    final List<Entity> toBeRemoved = new ArrayList<>();
    Long dpId;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--doit")) {
                doDelete = true;
            } else {
                dpId = Long.parseLong(args[i]);
            }
        }

        System.out.println("Deleting DP #" + dpId);
        final List<Key> terminate = new ArrayList<>();

        Key k = KeyFactory.createKey("SurveyedLocale", dpId);
        terminate.add(k); //SL itself

        //Now get any contributing form instances, based on their parent pointer
        final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL, dpId));
        final PreparedQuery pqi = ds.prepare(qi);
        for (Entity fi : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            System.out.println("  Deleting FI #" + fi.getKey().getId() + " with any QAs" );
            terminate.add(fi.getKey());
            //Now get the QASs for this FI
            terminate.addAll(qasForInstanceK(ds, fi.getKey().getId()));
        }

        if (doDelete) {
            System.out.println("Deleting " + terminate.size() + " entities");
            DataUtils.batchDelete(ds, terminate);
        } else {
            System.out.println("Not deleting " + terminate.size() + " entities");
        }
    }


    private List<Key> qasForInstanceK(DatastoreService ds, Long siId) {
        final Query qasq = new Query("QuestionAnswerStore").setFilter(new Query.FilterPredicate("surveyInstanceId", FilterOperator.EQUAL, siId));
        final PreparedQuery pqasq = ds.prepare(qasq);
        List<Key> result = new ArrayList<>();
        for (Entity qa : pqasq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey());
        }
        return result;
    }



}
