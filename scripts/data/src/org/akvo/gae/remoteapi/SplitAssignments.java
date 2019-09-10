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

import static org.akvo.gae.remoteapi.DataUtils.batchSaveEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/*
 * - Find assignments that cross survey boundaries, and split them
 */
public class SplitAssignments implements Process {

    private boolean doit = false;
    private final List<Entity> toBeSaved = new ArrayList<>();
    private final List<Entity> toBeCreated = new ArrayList<>();
    private final Set<Long> surveys = new HashSet<>();
    private final Set<Long> forms = new HashSet<>();
    private final Map<Long, Long> surveyOfForm = new HashMap<>();
    private final Map<Long, String> nameOfSurvey = new HashMap<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                doit = true;
            }
        }

        processSurveys(ds);
        processForms(ds);
        processAssignments(ds);
    }


    private void processAssignments(DatastoreService ds) {
        final Query q = new Query("SurveyAssignment");
        final PreparedQuery pq = ds.prepare(q);

        //Loop over assignments
        for (Entity ass : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            boolean splitAllowed = true;
            boolean forceSplit = true; //want to write the surveyId and new field names to all
            Long id = ass.getKey().getId();
            String name = (String) ass.getProperty("name");
            Map<Long, List<Long>> surveys = new HashMap<>(); //From survey ids to a list of forms

            @SuppressWarnings("unchecked")
            List<Long> forms = (List<Long>) ass.getProperty("surveyIds");
            if (forms == null) {
                continue;
            }

            for (Long formId: forms) {
                if (formId == 0) { //obviously bogus; fix it
                    System.out.println("ERROR! Form in assignment is 0; removing it " + formId);
                    forceSplit = true;
                    continue;
                }
                Long surveyId = surveyOfForm(ds, formId);
                if (surveyId > 0L) { //Good
                    if (surveys.containsKey(surveyId)) {
                        surveys.get(surveyId).add(formId);
                    } else {
                        List<Long>formList = new ArrayList<Long>();
                        formList.add(formId);
                        surveys.put(surveyId,formList);
                    }

                } else if (surveyId == -1) { //Survey structure needs repair; leave it alone
                    System.out.println("ERROR! Form " + formId + " in assignment is in a nonexistent survey");
                    splitAllowed = false;
                } else {
                    System.out.println("ERROR! Nonexistent form " + formId + " in assignment " + id);
                    forceSplit = true; //repair the assignment
                }
            }

            if (splitAllowed && (forceSplit || surveys.size() > 1)) { //Must be split!
                System.out.println("Rewriting assignment " + id + " in " + surveys.size() + " pieces");

                int part = 0;
                for (Entry<Long, List<Long>> entry: surveys.entrySet()) {
                    Long surveyId = entry.getKey();
                    List<Long> formList = entry.getValue();
                    part++;
                    if (part == 1) { //change it
                        System.out.println(" changing from " + ass);
                        ass.setProperty("name", name + " [" + nameOfSurvey.get(surveyId) + "]");
                        ass.removeProperty("surveyIds");
                        ass.setProperty("formIds", formList);
                        ass.setProperty("surveyId", surveyId);
                        System.out.println(" changing to " + ass);
                        toBeSaved.add(ass);
                    } else { // make a new one
                        Entity newAss = new Entity("SurveyAssignment");
                        newAss.setPropertiesFrom(ass);
                        newAss.setProperty("name", name + " [" + nameOfSurvey.get(surveyId) + "]");
                        newAss.setProperty("formIds", formList);
                        newAss.setProperty("surveyId", surveyId);
                        System.out.println(" creating " + newAss);
                        toBeCreated.add(newAss);
                    }
                }
            }
        }
        System.out.println("Saving   " + toBeSaved.size() + " entities.");
        System.out.println("Creating " + toBeCreated.size() + " entities.");
        if (doit) {
            batchSaveEntities(ds, toBeSaved);
            ds.put(toBeCreated);
        } else {
            System.out.println("...not!");
        }
    }


    private void processSurveys(final DatastoreService ds) {
        final Query q = new Query("SurveyGroup");
        final PreparedQuery pq = ds.prepare(q);

        //Loop over forms
        for (Entity survey : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            Long id = survey.getKey().getId();
            String type = (String) survey.getProperty("projectType");
            String name = (String) survey.getProperty("name");
            //Check that it is a survey
            if ("PROJECT".contentEquals(type)) {
                surveys.add(id);
                nameOfSurvey.put(id, name);
            }
        }
    }


    private void processForms(final DatastoreService ds) {
        final Query q = new Query("Survey");
        final PreparedQuery pq = ds.prepare(q);

        //Loop over forms
        for (Entity form : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            Long id = form.getKey().getId();
            Long surveyId = (Long) form.getProperty("surveyGroupId");
            forms.add(id);
            //Check that it exists
            if (surveys.contains(surveyId)) {
                surveyOfForm.put(id,  surveyId);
            }
        }
    }


    private Long surveyOfForm(final DatastoreService ds, final Long formId) {
        if (forms.contains(formId)) {
            Long surveyId = surveyOfForm.get(formId);
            if (surveyId == null) {
                return -1L; //form is in bad survey
            }
            return surveyId;
        }
        return -2L; //No such form
    }
}
