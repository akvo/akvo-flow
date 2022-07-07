    /*
     *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* Generate stats for web forms submissions.  A helper script is used that wraps up necessary parameters
* Assumes that the webforms always has as a submitter "Akvo Flow Web"
 */
public class InstanceWithWebFormSubmissions implements Process {
        @Override
        public void execute(DatastoreService ds, String[] args) throws Exception {
            String appId = ds.allocateIds("Survey", 1).getStart().getAppId(); // In the case where there are no SurveyInstances found
                                                                                    // We need this to get the appId
            if (args.length != 1) {
                System.out.println("Missing date parameter expected");
                System.exit(1);
            }

            Date date = parseDateArg(args[0]);

            List<Entity> formInstanceKeys = findFormInstancesWithWebforms(ds, date);
            if (formInstanceKeys.isEmpty()) {
                System.out.println(appId + ",0");
            } else {
                System.out.println(appId + "," + formInstanceKeys.size());
            }
        }

        private Date parseDateArg(String dateStr) {
            LocalDate date = LocalDate.parse(dateStr);
            ZoneId zoneId = ZoneId.of("GMT");
            return Date.from(date.atStartOfDay(zoneId).toInstant());
        }

        private List<Entity> findFormInstancesWithWebforms(DatastoreService ds, Date date) {
            Query.Filter filter = new Query.FilterPredicate("deviceIdentifier", Query.FilterOperator.EQUAL, "Akvo Flow Web");
            Query.Filter dateFilter = new Query.FilterPredicate("collectionDate", Query.FilterOperator.LESS_THAN, date);
            Query.Filter combined = Query.CompositeFilterOperator.and(filter, dateFilter);

            Query findFormInstances = new Query("SurveyInstance")
                    .setFilter(combined)
                    .setKeysOnly();
            PreparedQuery pq = ds.prepare(findFormInstances);

            List<Entity> instances = new ArrayList<>();
            for (Entity instance : pq.asIterable(FetchOptions.Builder.withDefaults())) {
                instances.add(instance);
            }
            return instances;
        }
    }
