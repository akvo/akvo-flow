/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
 * - For ech SurveyAssignment.deviceIds should have a DataPointAssigment entity related, when missing create a new one with dataPointsIds=["0"]
 */
public class UnifyDataPointAssignment implements Process {

    private Entity getDataPointAssignment(DatastoreService ds, long deviceId, long surveyAssignmentId, long surveyId) {
        Query.Filter f1 = new FilterPredicate("deviceId", FilterOperator.EQUAL, deviceId);
        Query.Filter f2 = new FilterPredicate("surveyAssignmentId", FilterOperator.EQUAL, surveyAssignmentId);
        Query.Filter f3 = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
        Query q = new Query("DataPointAssignment");
        q.setFilter(Query.CompositeFilterOperator.and(f1, f2, f3));
        PreparedQuery pq = ds.prepare(q);
        return pq.asSingleEntity();
    }

    private void assignEntityProp(Entity origin, Entity target, String prop) {
        target.setProperty(prop, origin.getProperty(prop));
    }

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final Query q = new Query("SurveyAssignment");
        final PreparedQuery pq = ds.prepare(q);
        final List<Entity> toBeCreated = new ArrayList<>();

        System.out.println("Processing SurveyAssignments");

        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            final Long surveyAssignmentId = sl.getKey().getId();
            final List<Long> deviceIds = (List<Long>) sl.getProperty("deviceIds");
            final Long surveyId = (Long) sl.getProperty("surveyId");
            if (deviceIds == null) {
                continue;
            }
            for (Long deviceId : deviceIds) {
                try {
                    final Entity dataPointAssignement = getDataPointAssignment(ds, deviceId, surveyAssignmentId, surveyId);
                    if (dataPointAssignement == null) {

                        Entity newDataPointAssignment = new Entity("DataPointAssignment");

                        newDataPointAssignment.setProperty("dataPointIds", Collections.singletonList(0L));

                        newDataPointAssignment.setProperty("deviceId", deviceId);
                        newDataPointAssignment.setProperty("surveyId", surveyId);
                        newDataPointAssignment.setProperty("surveyAssignmentId", surveyAssignmentId);

                        assignEntityProp(sl, newDataPointAssignment, "createUserId");
                        assignEntityProp(sl, newDataPointAssignment, "lastUpdateUserId");

                        newDataPointAssignment.setProperty("ancestorIds", null);

                        final Date date = new Date();
                        newDataPointAssignment.setProperty("createdDateTime", date);
                        newDataPointAssignment.setProperty("lastUpdateDateTime", date);

                        newDataPointAssignment.setProperty("lastUpdateUserId", 0);
                        newDataPointAssignment.setProperty("createUserId", 0);

                        System.out.println("DataPointAssignment to be created: " + newDataPointAssignment);

                        toBeCreated.add(newDataPointAssignment);
                    }
                } catch (Exception e) {
                    System.out.println("Error trying to process following values, deviceId: " + deviceId + " surveyAssignmentId: " + surveyAssignmentId + " surveyId: " + surveyId);
                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println("DataPointAssignment that should be created: " + toBeCreated.size());
        if (toBeCreated.size() > 0) {
            ds.put(toBeCreated);
        }
    }
}

