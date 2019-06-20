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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;

/*
 * - Find duplicated datapoints, output script responses to choose from
 */
public class DuplicatedSurveyedLocale implements Process {

    boolean showAnswers = false;
    boolean gatherDefault = true;
    boolean renameDefault = true;
    String instance;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private enum formRole {NONE, SINGLE, REGISTRATION, MONITORING};

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.println("#!/bin/bash");

        for (int i = 0; i < args.length; i++) {
            System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--answers")) {
                showAnswers = true;
            } else if (args[i].equalsIgnoreCase("--nogather")) {
                gatherDefault = false;
            } else if (args[i].equalsIgnoreCase("--norename")) {
                renameDefault = false;
            } else {
                instance = args[i];
            }
        }

        df.setTimeZone(TimeZone.getTimeZone("UT"));

        final Query q = new Query("SurveyedLocale").addSort("identifier", SortDirection.ASCENDING);
        final PreparedQuery pq = ds.prepare(q);

        String lastIdentifier = null;
        Entity lastSL = null;
        int count = 0, suspectCount = 0;
        List<Entity> toBeJudged = new ArrayList<>();

        //Loop over sorted list; any adjacent entries w same identifier means duplicate
        for (Entity sl : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            count++;
            String identifier = (String) sl.getProperty("identifier");
            if (identifier == null) identifier = "";

            if (identifier.equals(lastIdentifier)) { //Duplicate!
                //Add both to the list (unless there, in case there are more than 2)
                suspectCount++;
                toBeJudged.add(sl);
                if (!toBeJudged.contains(lastSL)) {
                    suspectCount++;
                    toBeJudged.add(lastSL);
                }
            } else if (toBeJudged.size()>0) {
                handleClone(ds, toBeJudged);
                toBeJudged = new ArrayList<>();
            }

            lastIdentifier = identifier;
            lastSL = sl;
        }
        System.out.println("##Scanned " + count + " DPs. Found " + suspectCount + " suspects.");

    }


    //Deal w a group of DPs with identical identifiers
    private void handleClone(DatastoreService ds, List<Entity>toBeJudged) {

        int identicalAnswers = 0;
        String lastAnswers = null;
        //Keep track of the surveys involved
        Set<Long> surveyIds = new HashSet<Long>();

        int globalRegCount = 0;
        int globalMonCount = 0;
        int globalSingleCount = 0;

        List<String> globalAnswerList = new ArrayList<>();

        System.out.println("");
        System.out.println("");

        for (Entity sl: toBeJudged) {
            if (sl == null) {
                System.out.println("#Error! Judging Null entity!");
                continue;
            }
            if (sl.getKey() == null) {
                System.out.println("#Error! Null in key evaluation!");
                continue;
            }

            Long id = sl.getKey().getId(); //NPE?!
            String identifier = (String) sl.getProperty("identifier");
            if (identifier == null) identifier = "";
            Date created = (Date) sl.getProperty("createdDateTime");
            Long survey = (Long) sl.getProperty("surveyGroupId");

            //Decision support:
            System.out.println(String.format(
                    "##Datapoint '%s'  survey %d  created %s ",
                    identifier,
                    survey,
                    df.format(created)
                    ));

            //Now show any contributing surveyInstances
            final Query qi = new Query("SurveyInstance").setFilter(new Query.FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL, id));
            final PreparedQuery pqi = ds.prepare(qi);
            int siCount = 0;
            int regCount = 0;
            int monCount = 0;
            int noneCount = 0;
            int singleCount = 0;
            boolean doDefaults = true;
            int duplicates = 0;

            for (Entity si : pqi.asIterable(FetchOptions.Builder.withChunkSize(500))) {
                siCount++;
                List<String> answers = qasValuesForInstance(ds, si.getKey().getId());
                String answersString = "(" + answers.size() + ") " + answers.toString();
                if (globalAnswerList.contains(answersString)) { //Duplicate!
                    duplicates++;
                } else globalAnswerList.add(answersString);

                Date siCre = (Date) si.getProperty("createdDateTime");
                Long siId = si.getKey().getId();
                Long formId = (Long) si.getProperty("surveyId");
                Long surveyId = surveyOfForm(ds, formId);
                surveyIds.add(surveyId);
                formRole role = registrationForm(ds, formId, surveyId);
                if (role==formRole.REGISTRATION) {
                    regCount++;  globalRegCount++;
                }
                if (role==formRole.SINGLE) {
                    singleCount++; globalSingleCount++;
                }
                if (role==formRole.MONITORING) {
                    monCount++; globalMonCount++;
                }
                if (role==formRole.NONE) noneCount++;

                String s = String.format(
                        "  ## FI %d  form %d (%s, survey %s) created %s",
                        siId,
                        formId,
                        role.toString(),
                        surveyId,
                        df.format(siCre));
                if (showAnswers) {
                    s += String.format(
                            "  answers %s",
                            answersString
                            );

                    }
                System.out.println(s);

                //Check if answers were the same as last FI
                if (lastAnswers != null && lastAnswers.equals(answersString)) {
                    identicalAnswers++;
                } else {
                    identicalAnswers = 0;
                }
                lastAnswers = answersString;
            }

            boolean allDup = (duplicates >= siCount);

            //Warnings
            if (surveyIds.size() > 1) {
                System.out.println("#Warning! More than one survey involved!");
                //doDefaults = false; //Want to delete duplicates anyway
            }
            if (singleCount > 1) {
                System.out.println("#Warning! More than one single-form instance involved!");
                doDefaults = false;
            }
            if (noneCount > 0) {
                System.out.println("#Warning! Some instances have invalid surveys!");
                doDefaults = false;
            }
            if (regCount > 1 || globalRegCount > 1) {
                System.out.println("#Warning! More than one registration-form instance involved!");
                doDefaults = false;
            }

            //Action options with defaults:
            //This line to be uncommented if DP should be removed. Default if no instances or all identical answers
            System.out.println(String.format(
                    "%s./my_delete-datapoint.sh %s %d%s",
                    (doDefaults && (siCount == 0 || allDup))?"":"#",
                    instance,
                    id,
                    " --doit"));
            //This line to be uncommented if DP should be renamed
            System.out.println(String.format(
                    "%s./my_reidentify-datapoint.sh %s %d%s",
                    (doDefaults && renameDefault && globalSingleCount > 1 && identicalAnswers == 0)?"":"#",
                    instance,
                    id,
                    " --doit"));
            //This line to be uncommented if DP should absorb all of the instances. Default if enabled and it has registration instances
            System.out.println(String.format(
                    "%s./my_gather-instances-to-datapoint.sh %s %d%s",
                    (doDefaults && gatherDefault && regCount > 0 && globalMonCount > 0)?"":"#",
                    instance,
                    id,
                    " --doit"));

        }
    }


    private List<String> qasValuesForInstance(DatastoreService ds, Long siId) {
        final Query qasq = new Query("QuestionAnswerStore")
                .setFilter(new Query.FilterPredicate("surveyInstanceId", FilterOperator.EQUAL, siId));
        final PreparedQuery pqasq = ds.prepare(qasq);
        Map<String, Entity> answers = new TreeMap<>();
        for (Entity qa : pqasq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            answers.put((String) qa.getProperty("questionID"), qa); //Note uppercase D, and type String
        }
        List<String> result = new ArrayList<>();
        for (Entity qa : answers.values()) { //TreeMap gives them in key order, so sorted by questionID (a string!)
            String s = (String) qa.getProperty("value");
            if (s == null) {
                Text t = (Text) qa.getProperty("valueText");
                if (t != null) {
                    s = t.getValue();
                }
            }
            if (s == null) {
                s = "";
            }
            result.add(s.replaceAll("\n", " ")); //keep it on one line!
//            System.out.println(String.format("     #### answer %s value '%s'",qa.getProperty("questionID"),qa.getProperty("value")));
        }
        return result;
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
            return 0L;
        }
        return (Long) form.getProperty("surveyGroupId");
    }

    private formRole registrationForm(DatastoreService ds, Long formId, Long surveyId) {
        if (formId == 0) {
            System.out.println(String.format(" ##Error! formId is 0"));
            return formRole.NONE;
        }
        if (surveyId == 0) {
            System.out.println(String.format(" ##Error! surveyId is 0"));
            return formRole.NONE;
        }
        Key k = KeyFactory.createKey("SurveyGroup", surveyId);
        Entity survey;
        try {
            survey = ds.get(k);
        } catch (EntityNotFoundException e) {
            return formRole.NONE;
        }
        Long regFormId = (Long) survey.getProperty("newLocaleSurveyId");
        Boolean monitoring = (Boolean) survey.getProperty("monitoringGroup");
        //Check to see if registration form
        if (monitoring == null || !monitoring) {
            return formRole.SINGLE;
        } else
        if (regFormId.equals(formId)) {
            return formRole.REGISTRATION;
        } else {
            return formRole.MONITORING;

        }
    }


}
