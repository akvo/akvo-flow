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

import static org.akvo.gae.remoteapi.DataUtils.batchSaveEntities;
import static org.akvo.gae.remoteapi.DataUtils.batchDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/*
 * - Checks that all surveys, groups, questions and options are consistent
 */
public class CheckSurveyStructure implements Process {

    // private static String ERR_MSG = "Unable to hide SurveyedLocale [%s], reason: %s";
    int e1 = 0, e2 = 0, e3 = 0, e4 = 0, e5 = 0;
    int goodQuestions = 0, goodOptions = 0;
    Map<Long, Long> qToSurvey = new HashMap<>();
    Map<Long, Long> qgToSurvey = new HashMap<>();

    boolean fixSurveyPointers = false; // Make question survey pointer match the group's
    boolean deleteOrphans = false;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: FIX to correct survey pointers, GC to delete orphaned entites.\n");
        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("FIX")) {
                fixSurveyPointers = true;
            }
            if (args[i].equalsIgnoreCase("GC")) {
                deleteOrphans = true;
            }
        }

        processGroups(ds);
        processQuestions(ds);
        processOptions(ds);

        System.out.printf("#QuestionGroups: %5d good, %4d surveyless\n", qgToSurvey.size(), e1);
        System.out.printf("#Questions:      %5d good, %4d groupless,    %4d unreachable\n", goodQuestions, e2, e3);
        System.out.printf("#OptionOptions:  %5d good, %4d questionless, %4d unreachable\n", goodOptions, e4, e5);

    }

    private void processGroups(DatastoreService ds) {

        System.out.println("#Processing Question Groups");

        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroupId = g.getKey().getId();
            Long questionGroupSurvey = (Long) g.getProperty("surveyId");
            String questionGroupName = (String) g.getProperty("name");
            if (questionGroupId == null) {
                System.out.printf("#ERR group %d '%s'not in a survey!\n", questionGroupId,
                        questionGroupName);
                e1++;
            } else {
                qgToSurvey.put(questionGroupId, questionGroupSurvey);
                // System.out.printf(" group %d -> survey %d\n",questionGroupId,
                // questionGroupSurvey);
            }
        }

    }

    private void processQuestions(DatastoreService ds) {
        System.out.println("#Processing Questions");

        final Query qq = new Query("Question");
        final PreparedQuery qpq = ds.prepare(qq);
        List<Entity> questionsToFix = new ArrayList<Entity>();
        List<Key> questionsToKill = new ArrayList<Key>();
        
        for (Entity q : qpq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionId = q.getKey().getId();
            Long questionSurvey = (Long) q.getProperty("surveyId");
            Long questionGroup = (Long) q.getProperty("questionGroupId");
            String questionText = (String) q.getProperty("text");
            Long questionGroupSurvey = (Long) qgToSurvey.get(questionGroup);


            if (questionGroup == null || questionGroupSurvey == null) { // in no or a nonexistent group; hopelessly lost
                System.out.printf("#ERR: Question %d '%s',survey %d, group %d\n",
                        questionId, questionText, questionSurvey, questionGroup);
                e2++;
                if (deleteOrphans){
                    System.out.println(q.toString());//for posterity
                    q.setProperty("surveyId", questionGroupSurvey);
                    questionsToKill.add(q.getKey());
                }
            } else { // check for wrong survey/qg
                qToSurvey.put(questionId, questionSurvey); //ok parent for options
                if (!questionSurvey.equals(questionGroupSurvey)) {
                    System.out.printf("#ERR: Question %d '%s' in survey %d, but group %d is in survey %d\n",
                            questionId, questionText, questionSurvey, questionGroup, questionGroupSurvey);
                    if (fixSurveyPointers){
                        System.out.println(q.toString());//for posterity
                        q.setProperty("surveyId", questionGroupSurvey);
                        questionsToFix.add(q);
                    }
                    e3++;
                } else {
                    goodQuestions++;
                }
            }
        }
        if (questionsToFix.size() > 0) {
            System.out.printf("#Fixing %d Questions\n",questionsToFix.size());
            batchSaveEntities(ds, questionsToFix);
        }
        if (questionsToKill.size() > 0) {
            System.out.printf("#Deleting %d Questions\n",questionsToKill.size());
            batchDelete(ds, questionsToKill);
        }
    }

    private void processOptions(DatastoreService ds) {
        System.out.println("#Processing Options");

        final Query oq = new Query("QuestionOption");
        final PreparedQuery opq = ds.prepare(oq);
        List<Key> optionsToKill = new ArrayList<Key>();

        for (Entity option : opq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long optionId = option.getKey().getId();
            Long questionId = (Long) option.getProperty("questionId");
            String optionText = (String) option.getProperty("text");

            if (questionId == null) { // check for no question
                System.out.printf("#ERR: Option %d '%s', not in a question\n", optionId, optionText);
                e4++;
                if (deleteOrphans) {
                    optionsToKill.add(option.getKey());
                    System.out.println(option.toString());//for posterity
                }
            } else { // check for bad question
                if (!qToSurvey.containsKey(questionId)) {
                    System.out.printf(
                            "#ERR: Option %d '%s' is in nonexistent question %d\n",
                            optionId, optionText, questionId);
                    e5++;
                    if (deleteOrphans) {
                        optionsToKill.add(option.getKey());
                        System.out.println(option.toString());//for posterity
                    }
                } else {
                    goodOptions++;
                }
            }
        }
        if (optionsToKill.size() > 0) {
            System.out.printf("#Deleting %d Options\n",optionsToKill.size());
            batchDelete(ds, optionsToKill);
        }

    }
}
