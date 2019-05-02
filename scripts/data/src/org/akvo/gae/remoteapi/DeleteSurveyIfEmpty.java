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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Delete a survey (SurveyGroup with projectType=PROJECT) if it has no forms
 */
public class DeleteSurveyIfEmpty implements Process {

    boolean doit = false;
    final List<Key> toBeRemoved = new ArrayList<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        Long surveyId = null;

        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                doit = true;
            } else {
                surveyId = Long.parseLong(args[i]);
            }
        }
        if (surveyId == null) {
            System.out.println("No Survey id parameter given!");
            return;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UT"));

        try { //Has to exist
            Entity survey = ds.get(KeyFactory.createKey("SurveyGroup", surveyId));
            toBeRemoved.add(survey.getKey());
            if (! "PROJECT".equals(survey.getProperty("projectType"))) { //Has to be survey
                System.out.println("Survey not found: " + surveyId);
                return;
            }
            //Has to be empty
            List<Key> formList = formsOfSurvey(ds,surveyId);
            if (formList.size() > 0) {
                System.out.println("Survey " + surveyId + " not empty. Forms: " + formList);
                return;
            }
            if (doit) {
                System.out.println("Deleting survey " + surveyId);
                ds.delete(toBeRemoved);
            } else {
                System.out.println("Not deleting survey " + surveyId);
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Survey not found: " + surveyId);
            return;
        }
    }

    private List<Key> formsOfSurvey(DatastoreService ds, Long qId) {
        final Query optq = new Query("Survey").setFilter(new Query.FilterPredicate("surveyGroupId", FilterOperator.EQUAL, qId));
        final PreparedQuery pqopt = ds.prepare(optq);
        List<Key> result = new ArrayList<>();
        for (Entity qa : pqopt.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey());
        }
        return result;
    }
}
