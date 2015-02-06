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

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class PublicPrivateInstanceCount implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {


        int chunkSize = Integer.valueOf(args[0]);
        String appId = args[1];
        Query sq = new Query("Survey");
        Map<Long, Entity> sgs = new HashMap<Long, Entity>();
        for (Entity s : ds.prepare(sq).asIterable(FetchOptions.Builder.withChunkSize(chunkSize))) {

            Long surveyGroupId = (Long) s.getProperty("surveyGroupId");

            if (surveyGroupId == null) {
                continue;
            }

            if (!sgs.containsKey(surveyGroupId)) {
                Filter f = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL,
                        KeyFactory.createKey("SurveyGroup", surveyGroupId));
                Query qsg = new Query("SurveyGroup").setFilter(f);
                Entity sg = ds.prepare(qsg).asSingleEntity();
                sgs.put(surveyGroupId, sg);
            }

            String type = "PRIVATE";
            Entity sg = sgs.get(surveyGroupId);
            if (sg != null) {
                String pl = (String) sg.getProperty("privacyLevel");
                if (pl == null) {
                    type = getPrivacyLevelFromSurvey(s);
                } else {
                    type = pl.toUpperCase();
                }
            } else {
                type = getPrivacyLevelFromSurvey(s);
            }
            Filter fsi = new FilterPredicate("surveyId", FilterOperator.EQUAL, s.getKey().getId());
            Query si = new Query("SurveyInstance").setFilter(fsi).setKeysOnly();
            long count = 0;
            for (@SuppressWarnings("unused")
            Entity sie : ds.prepare(si).asIterable(
                    FetchOptions.Builder.withChunkSize(chunkSize))) {
                count++;
            }
            String out = String.format("%s,%s,%s,%s,%s", appId, s.getKey().getId(),
                    s.getProperty("name"),
                    type, count);
            System.out.println(out);
        }

    }

    private String getPrivacyLevelFromSurvey(Entity s) {
        String pointType = (String) s.getProperty("pointType");
        return "Household".equals(pointType) ? "PRIVATE" : "PUBLIC";
    }

}
