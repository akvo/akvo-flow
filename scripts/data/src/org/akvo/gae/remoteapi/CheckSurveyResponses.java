/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Checks that all SurveyInstance, QuestionAnswerStore, SurveyedLocale items are consistent
 */
public class CheckSurveyResponses implements Process {

    private int orphanInstances = 0, orphanLocales = 0, orphanSurveys = 0;
//    private int goodQuestions = 0, goodOptions = 0;

    private Map<Long, String> surveyGroups = new HashMap<>();
    private Map<Long, String> surveys = new HashMap<>();
    private Map<Long, Long> surveyToSurveyGroup = new HashMap<>();
    private List<Long> targetSurveys = new ArrayList<>();

    private Map<Long, String> instanceUuid = new HashMap<>();
    private Map<Long, Long> instanceToSurveyedLocale = new HashMap<>();
    
    private Map<Long, String> localeIdentifier = new HashMap<>();
    private Map<Long, Long> localeToSurveyGroup = new HashMap<>();
    private Set<Long> referredLocales = new HashSet<>();

     @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        if (args.length == 0) {
            System.err
                    .println("Usage: "
                            + RemoteAPI.class.getName()
                            + "CheckSurveyedResponses <appid> <username> <password> <appid> <surveyId>");
            System.exit(1);
        }

        Long theSurvey = Long.parseLong(args[0]);

        processSurveyGroups(ds);
        processSurveys(ds);
        
        //look up the surveyGroup TODO: nullcheck
        Long theSurveyGroup = surveyToSurveyGroup.get(theSurvey);
        //make a list of the surveys in the group
        for (Long s: surveyToSurveyGroup.keySet()) {
            Long g = surveyToSurveyGroup.get(s);
            if (g.equals(theSurveyGroup)) {
                targetSurveys.add(s);
            }
        }

        processLocales(ds, theSurveyGroup);
        System.out.printf("#SUM surveys in this group: %d\n", targetSurveys.size());
        for (Long s: targetSurveys) {
            instanceToSurveyedLocale.clear();
            processInstances(ds, s); //see which match the locales
            for (Long i: instanceToSurveyedLocale.keySet()) {
                processAnswers(ds,i);
            }
        }
        System.out.printf("#SurveyGroups:      %5d type PROJECT\n", surveyGroups.size());
        System.out.printf("#Surveys:           %5d total, %4d groupless, %4d in group\n", surveys.size(), orphanSurveys, targetSurveys.size());
        System.out.printf("#SurveyedLocales:   %5d total, %4d groupless, %4d referred by this form\n",
                localeIdentifier.size(), orphanLocales, referredLocales.size());
        System.out.printf("#SurveyInstances:   %5d good,  %4d localeless\n", instanceUuid.size(), orphanInstances);

    }

    //Need to know which groups exist



    private void processInstances(DatastoreService ds, Long aSurveyId) {

        System.out.printf("#Fetching SurveyInstances for survey %d\n", aSurveyId);

        //select only entities for the survey
        Filter formFilter = new Query.FilterPredicate("surveyId", FilterOperator.EQUAL, aSurveyId);
        final Query instance_q = new Query("SurveyInstance").setFilter(formFilter);

        final PreparedQuery survey_pq = ds.prepare(instance_q);

        for (Entity si : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long siId = si.getKey().getId();
            String surveyUuid = (String) si.getProperty("uuid");
            Long surveyedLocaleId = (Long) si.getProperty("surveyedLocaleId");
            Long surveyOfInstance = (Long) si.getProperty("surveyId");
            if (surveyOfInstance == null) { 
                System.out.printf("#ERR: SurveyInstance %d has null surveyId\n", siId);
                orphanInstances++;
            } else
            if (surveyedLocaleId == null) { 
                    System.out.printf("#ERR: SurveyInstance %d has null SurveyedLocation\n", siId);
                    orphanInstances++;
            } else {
                //see if it has a valid SL
                String ident = localeIdentifier.get(surveyedLocaleId);
                if (ident == null) {
                    System.out.printf("#ERR: SurveyInstance %d has nonexistent SurveyedLocation %d\n", siId, surveyedLocaleId );
                    orphanInstances++;
                } else {
                    referredLocales.add(surveyedLocaleId);
                    //See if that SL is in the SAME SurveyGroup
                    Long groupOfSl = localeToSurveyGroup.get(surveyedLocaleId);
                    Long groupOfInstance =  surveyToSurveyGroup.get(surveyOfInstance);
                    if (groupOfSl == null || groupOfInstance == null || !groupOfSl.equals(groupOfInstance)) {
                        System.out.printf("#ERR: SurveyInstance %d has group inconsistency: %d vs %d\n", siId, groupOfSl, groupOfInstance );                        
                    }
                    
                }
            }
            instanceUuid.put(siId, surveyUuid); //ok to have questions in
            instanceToSurveyedLocale.put(siId, surveyedLocaleId); //ok to have questions in
        }
        
    }

    private void processLocales(DatastoreService ds, Long groupId) {

        System.out.println("#Processing SurveyedLocales");

        //all or some of the locales
        final Query q = new Query("SurveyedLocale");
        if (groupId != null) { 
            Filter formFilter = new Query.FilterPredicate("surveyGroupId", FilterOperator.EQUAL, groupId);
            q.setFilter(formFilter);
        }
        final PreparedQuery survey_pq = ds.prepare(q);
        for (Entity sl : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long slId = sl.getKey().getId();
            String identifier = (String) sl.getProperty("identifier");
//            Long creatingSurvey = (Long) sl.getProperty("creationSurveyId");
            Long surveyGroup = (Long) sl.getProperty("surveyGroupId");
            localeIdentifier.put(slId, identifier);
            if (surveyGroup == null) {
                System.out.printf("#ERR: SurveyedLocale %d has null SurveyGroupId\n", slId );
                orphanLocales++;
            } else {
                localeToSurveyGroup.put(slId, surveyGroup);
            }
//          List<Long> contrib = (List<Long>) sl.getProperty("surveyInstanceContrib");
        }
    }
    
    private void processSurveyGroups(DatastoreService ds) {

        System.out.println("#Processing Survey Groups");

        //all the groups
        final Query instance_q = new Query("SurveyGroup");
        final PreparedQuery survey_pq = ds.prepare(instance_q);
        for (Entity sg : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long slId = sg.getKey().getId();
            String type = (String) sg.getProperty("projectType");
            //System.out.println("Type: " + type);
            if (type.equalsIgnoreCase("PROJECT")) {//a "Survey"
                String name = (String) sg.getProperty("name");
                surveyGroups.put(slId, name);
            }
        }
        
    }

    private void processSurveys(DatastoreService ds) {

        System.out.println("#Processing Surveys");

        final Query survey_q = new Query("Survey");
        final PreparedQuery survey_pq = ds.prepare(survey_q);

        for (Entity s : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyId = s.getKey().getId();
            String surveyName = (String) s.getProperty("name");
            Long surveyGroup = (Long) s.getProperty("surveyGroupId");
            if (surveyGroup == null) {
                System.out.printf("#ERR survey %d '%s' is not in a survey group\n",
                        surveyId, surveyName);
                orphanSurveys++;
            } else {
                surveys.put(surveyId,surveyName);
                surveyToSurveyGroup.put(surveyId,surveyGroup);                
            }
        }
    }

    private void processAnswers(DatastoreService ds, Long targetSurveyInstance) {

        System.out.printf("# Processing Answers for Survey instance %d\n", targetSurveyInstance);
        Filter filter = new Query.FilterPredicate("surveyInstanceId", FilterOperator.EQUAL, targetSurveyInstance);
        final Query q = new Query("QuestionAnswerStore").setFilter(filter);
        final PreparedQuery pq = ds.prepare(q);
        int answerCount = 0;
        for (Entity a : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            Long answerId = a.getKey().getId();
            answerCount++;
        }
        System.out.printf("#SUM %d answers\n", answerCount);
    }

}
