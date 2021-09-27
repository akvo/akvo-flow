/*
 *  Copyright (C) 2017,2019 Stichting Akvo (Akvo Foundation)
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
import java.util.Map;
import java.util.HashMap;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 */
public class GetDatapointCounts implements Process {

    private Map<Long, String> sgName = new HashMap<>();
    private Map<Long, String> sgType = new HashMap<>();
    private Map<Long, Long> sgParents = new HashMap<>();

    private Map<Long, String> surveyNames = new HashMap<>();
    private Map<Long, Long> surveyParents = new HashMap<>();
    private Map<Long, Long> surveyCounts = new HashMap<>();
    private Map<Long, List<Long>> surveyToGroups = new HashMap<>();

    private Map<Long, String> qNames = new HashMap<>();
    private Map<Long, String> qTypes = new HashMap<>();
    private Map<Long, Long> qParents = new HashMap<>();
    private Map<Long, Long> qToSurvey = new HashMap<>();
    private Map<Long, Long> qOrder = new HashMap<>();
    private Map<Long, Boolean> qMandatory = new HashMap<>();

    private Map<Long, String> qgNames = new HashMap<>();
    private Map<Long, Long> qgOrder= new HashMap<>();
    private Map<Long, Long> qgParents = new HashMap<>();
    private Map<Long, List<Long>> qgToQuestions = new HashMap<>();

    private Map<Long, String> oNames = new HashMap<>();
    private Map<Long, Long> oParents = new HashMap<>();

    private boolean html = false;
    private boolean showQuestions = false;
    private boolean showOptions = false;

    @Override
    public void execute(DatastoreService ds) throws Exception {

        System.out.printf("Form ID, Form Name, Survey Name, Total Form Instance, Path\n");
        fetchSurveyGroups(ds);
        fetchSurveys(ds);
        drawSurveyGroupsIn(0L, "");

    }

    private void drawSurveyGroupsIn(Long parent, String parentName) {
        for (Long sg : sgParents.keySet()) {
            if (sgParents.get(sg).equals(parent)) {
                if (sgType.get(sg).equals("PROJECT")) {
                    drawFormsIn(sg, sgName.get(sg), parentName);
                } else {
                    if (parentName != "") {
                        parentName += " > ";
                    }
                    parentName += sgName.get(sg);
                    drawSurveyGroupsIn(sg, parentName);
                }
            }
        }
    }

    private void drawFormsIn(Long parent, String surveyName, String parentName) {
        for (Long survey : surveyParents.keySet()) {
            if (surveyParents.get(survey).equals(parent)) {
                System.out.printf("%d,%s,%s,%d,%s,\n", survey, surveyName, surveyNames.get(survey), surveyCounts.get(survey), parentName);
            }
        }
    }

    private void fetchSurveyGroups(DatastoreService ds) {

        final Query q = new Query("SurveyGroup");
        final PreparedQuery pq = ds.prepare(q);

        for (Entity g : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyGroupId = g.getKey().getId();
            Long parentId = (Long) g.getProperty("parentId");
            String type = (String) g.getProperty("projectType");
            String name = (String) g.getProperty("name");
            if (parentId == null) {
            } else {
                sgParents.put(surveyGroupId, parentId);
                sgName.put(surveyGroupId, name);
                sgType.put(surveyGroupId, type);
            }
        }
    }

    private void fetchSurveys(DatastoreService ds) {


        final Query survey_q = new Query("Survey");
        final PreparedQuery survey_pq = ds.prepare(survey_q);

        for (Entity s : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyId = s.getKey().getId();
            String surveyName = (String) s.getProperty("name");
            Long surveyGroup = (Long) s.getProperty("surveyGroupId");
            if (surveyGroup == null) {
            } else {
                surveyNames.put(surveyId,surveyName);
                surveyParents.put(surveyId, surveyGroup);
                surveyToGroups.put(surveyId, new ArrayList<Long>() );
            }
            Filter fsi = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
            Query si = new Query("SurveyInstance").setFilter(fsi).setKeysOnly();
            long count = 0;
            for (@SuppressWarnings("unused")
                    Entity sie : ds.prepare(si).asIterable(
                        FetchOptions.Builder.withChunkSize(500))) {
                count++;
                        }
            surveyCounts.put(surveyId, count);
        }
    }

}
