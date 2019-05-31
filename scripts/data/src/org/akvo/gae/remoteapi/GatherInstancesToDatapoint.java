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
import java.util.UUID;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Gather all form instances under the datapoint with the same identifier
 */
public class GatherInstancesToDatapoint implements Process {

    boolean doChange = false;
    final List<Entity> toBeSaved = new ArrayList<>();
    Long dpId;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--doit")) {
                doChange = true;
            } else {
                dpId = Long.parseLong(args[i]);
            }
        }

        Key k = KeyFactory.createKey("SurveyedLocale", dpId);
        Entity datapoint = ds.get(k);
        String identifier = (String) datapoint.getProperty("identifier");
        @SuppressWarnings("unchecked")
        List<Long> contrib = (List<Long>) datapoint.getProperty("surveyInstanceContrib");


        //Now reparent any form instances with the same identifier
        final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleIdentifier", FilterOperator.EQUAL, identifier));
        final PreparedQuery pqi = ds.prepare(qi);
        for (Entity si : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            Long oldParent = (Long) si.getProperty("surveyedLocaleId");
            if (!dpId.equals(oldParent)) {
                System.out.println("Reparenting SI #" + si.getKey().getId());
                si.setProperty("surveyedLocaleId", dpId);
                toBeSaved.add(si);
                contrib.add(si.getKey().getId());
            }
        }

        if (toBeSaved.size() > 0) {
            datapoint.setProperty("surveyInstanceContrib", contrib);
            toBeSaved.add(datapoint);
        }

        if (doChange) {
            System.out.println("Changing " + toBeSaved.size() + " entities");
            DataUtils.batchSaveEntities(ds, toBeSaved);
        } else {
            System.out.println("Not changing " + toBeSaved.size() + " entities");
        }
    }

}
