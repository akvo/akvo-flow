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
 * - Delete a form with all it's groups, questions and options
 */
public class DeleteFormCompletely implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        boolean doit = false;
        Long surveyId = null;
        final List<Key> toBeRemoved = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                doit = true;
            } else {
                surveyId = Long.parseLong(args[i]);
            }
        }
        if (surveyId == null) {
            System.out.println("No Form id parameter given!");
            return;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UT"));

        System.out.println("#Processing form " + surveyId);
        if (!doit) {
            System.out.println(" (report only)");
        }

        try { //May be linked entities around, even if the form is not
            Entity form = ds.get(KeyFactory.createKey("Survey", surveyId));
            toBeRemoved.add(form.getKey());
        }
        catch (EntityNotFoundException e) {
            System.out.println("Form not found: " + surveyId);
        }

        final Query query1 = new Query("QuestionGroup")
                .setFilter(new Query.FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId))
                .setKeysOnly();
        final PreparedQuery pq = ds.prepare(query1);

        for (Entity qg : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            toBeRemoved.add(qg.getKey());
        }

        final Query query2 = new Query("Question")
                .setFilter(new Query.FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId))
                .setKeysOnly();
        final PreparedQuery pq2 = ds.prepare(query2);

        for (Entity q : pq2.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            if ("OPTION".equals(q.getProperty("type"))) {
                toBeRemoved.addAll(optionsOfQuestion(ds, q.getKey().getId()));
            }
            toBeRemoved.add(q.getKey());
        }


        if (doit) {
            System.out.println("Deleting " + toBeRemoved.size() + " entities.");
            DataUtils.batchDelete(ds,toBeRemoved);
        } else {
            System.out.println("Not deleting " + toBeRemoved.size() + " entities.");
        }

        //Assignments are not deleted, just modified
        removeFormFromAssignments(ds, surveyId, doit);
    }


    private List<Key> optionsOfQuestion(DatastoreService ds, Long qId) {
        final Query optq = new Query("QuestionOptions")
                .setFilter(new Query.FilterPredicate("questionId", FilterOperator.EQUAL, qId))
                .setKeysOnly();
        final PreparedQuery pqopt = ds.prepare(optq);
        List<Key> result = new ArrayList<>();
        for (Entity qa : pqopt.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            result.add(qa.getKey());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void removeFormFromAssignments(DatastoreService ds, Long surveyId, boolean doit) {
        final List<Entity> toBeSaved = new ArrayList<>();
        final Query query = new Query("SurveyAssignment").setFilter(new Query.FilterPredicate("surveyIds", FilterOperator.EQUAL, surveyId));
        final PreparedQuery pquery = ds.prepare(query);
        for (Entity ass : pquery.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            List<Long> forms = (List<Long>) ass.getProperty("surveyIds");
                if (forms == null) { //weird
                    System.out.println("Assignment with no forms:" + ass);
                } else {
                    forms.remove(surveyId);
                    ass.setProperty("surveyIds", forms);
                    toBeSaved.add(ass);
                }
        }
        if (doit) {
            System.out.println("Removing from " + toBeSaved.size() + " assignments.");
            DataUtils.batchSaveEntities(ds,toBeSaved);
        } else {
            System.out.println("Found in " + toBeSaved.size() + " assignments.");
        }
    }

}
