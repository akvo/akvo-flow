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
import java.util.List;
import java.util.Map;
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

    boolean doit = false;
    final List<Entity> toBeSaved = new ArrayList<>();
    final List<Entity> toBeCreated = new ArrayList<>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--doit")) {
                doit = true;
            }
        }

        final Query q = new Query("SurveyAssignment");
        final PreparedQuery pq = ds.prepare(q);

        //Loop over assignments
        for (Entity ass : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            boolean splitAllowed = true;
            boolean forceSplit = false;
            Long id = ass.getKey().getId();
            String name = (String) ass.getProperty("name");
            Map<Long, List<Long>> surveys = new HashMap<>(); //From survey ids to a list of forms

            @SuppressWarnings("unchecked")
            List<Long> forms = (List<Long>) ass.getProperty("surveyIds");
            if (forms == null) {
                continue;
            }

            for (Long formId: forms) {
                if (formId == 0){
                    System.out.println("ERROR! Form in assignment is 0; removing it " + formId);
                    forceSplit = true;
                    continue;
                }
                Long surveyId = surveyOfForm(ds, formId);
                if (surveyId > 0L) { //Good
                    if (surveys.containsKey(surveyId)) {
                        surveys.get(surveyId).add(formId);
                    } else {
                        List<Long>formList =new ArrayList<Long>();
                        formList.add(formId);
                        surveys.put(surveyId,formList);
                    }

                } else if (surveyId == -2){
                    System.out.println("ERROR! Form " + formId + " in assignment is in nonexistent survey");
                    splitAllowed = false;
                } else {
                    System.out.println("ERROR! Nonexistent form " + formId + " in assignment " + id);
                    forceSplit = true; //repair the assignment
                }
            }

            if (splitAllowed && (forceSplit || surveys.size() > 1)) { //Must be split!
                System.out.println("Splitting assignment " + id + " into " + surveys.size());

                int part = 0;
                for (List<Long> f: surveys.values()) {
                    if (++part == 1) { //change it
                        toBeSaved.add(ass);
                        System.out.println(" changing from " + ass);
                        ass.setProperty("name", name + " [" + part + "]");
                        ass.setProperty("surveyIds", f);
                        System.out.println(" changing to " + ass);
                    } else { // make a new one
                        Entity newAss = new Entity("SurveyAssignment");
                        newAss.setPropertiesFrom(ass);
                        newAss.setProperty("name", name + " [" + part + "]");
                        newAss.setProperty("surveyIds", f);
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



    private Long surveyOfForm(final DatastoreService ds, final Long formId) {
        if (formId == 0) {
            System.out.println(String.format(" ##Error! formId is 0"));
            return 0L;
        }
        Key k = KeyFactory.createKey("Survey", formId);
        Entity form;
        try {
            form = ds.get(k);
        } catch (EntityNotFoundException e) {
            return -1L;
        }
        Long surveyId = (Long) form.getProperty("surveyGroupId");
        //Check that it exists
        Key k2 = KeyFactory.createKey("SurveyGroup", surveyId);
        Entity survey;
        try {
            survey = ds.get(k2);
        } catch (EntityNotFoundException e) {
            return -2L;
        }
        return surveyId;
    }

}
