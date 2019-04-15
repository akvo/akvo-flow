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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.util.List;

/*
 * - Checks missing files listed in DeviceFileJobQueue
 */
public class CheckMissingFiles implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) {
        Query deviceFileJobQueue = new Query("DeviceFileJobQueue");
        deviceFileJobQueue.addSort("createdDateTime", Query.SortDirection.DESCENDING);

        List<Entity> entities = ds.prepare(deviceFileJobQueue)
                .asList(FetchOptions.Builder.withLimit(100));
        System.out.println("files found " + entities.size() + " ");
        for (Entity e : entities) {
            final Entity device = getDevice(ds, e);
            final Entity dataPoint = getDataPoint(ds, e);
            String deviceIdentifier =
                    device != null ? (String) device.getProperty("deviceIdentifier") : "null";
            String surveyedLocaleIdentifier = dataPoint != null ? (String) dataPoint
                    .getProperty("surveyedLocaleIdentifier") : "null";
            System.out.println(
                    e.getProperty("fileName")
                            + ", " + e.getProperty("deviceId")
                            + ", " + deviceIdentifier
                            + ", " + surveyedLocaleIdentifier);
        }
    }

    private Entity getDataPoint(DatastoreService ds, Entity e) {
        if (e != null && e.getProperty("qasId") != null) {
            final Query.Filter fs = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                    Query.FilterOperator.EQUAL,
                    KeyFactory.createKey("QuestionAnswerStore", (Long) e.getProperty("qasId")));
            final Query qs = new Query("QuestionAnswerStore").setFilter(fs);
            Entity questionAnswer = ds.prepare(qs).asSingleEntity();

            final Query.Filter fs2 = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                    Query.FilterOperator.EQUAL, KeyFactory.createKey("SurveyInstance",
                    (Long) questionAnswer.getProperty("surveyInstanceId")));
            final Query qs2 = new Query("SurveyInstance").setFilter(fs2);
            return ds.prepare(qs2).asSingleEntity();
        } else {
            return null;
        }
    }

    private Entity getDevice(DatastoreService ds, Entity e) {
        final Query.Filter fs = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                Query.FilterOperator.EQUAL,
                KeyFactory.createKey("Device", (Long) e.getProperty("deviceId")));
        final Query qs = new Query("Device").setFilter(fs);
        return ds.prepare(qs).asSingleEntity();
    }
}
