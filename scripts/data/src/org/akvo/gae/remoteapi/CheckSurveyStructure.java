/*
 *  Copyright (C) 2017-2018 Stichting Akvo (Akvo Foundation)
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

    private int orphanSurveys = 0, orphanGroups = 0, orphanQuestions = 0, unreachableQuestions = 0, orphanOptions = 0;
    private int goodQuestions = 0, goodOptions = 0;
	private int goodTranslations = 0, orphanTranslations = 0;
    private Map<Long, String> surveys = new HashMap<>();
    private Map<Long, Long> qToSurvey = new HashMap<>();
    private Map<Long, Long> qToQg = new HashMap<>();
    private Map<Long, Long> qgToSurvey = new HashMap<>();

    private boolean fixSurveyPointers = false; // Make question survey pointer match the group's
    private boolean deleteOrphans = false;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: --fix to correct survey pointers of questions, --gc to delete orphaned entites.\n");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--fix")) {
                fixSurveyPointers = true;
            }
            if (args[i].equalsIgnoreCase("--gc")) {
                deleteOrphans = true;
            }
        }

        processAllSurveys(ds);
        processGroups(ds);
        processQuestions(ds);
        processOptions(ds);
        processTranslations(ds);

        System.out.printf("#Surveys:         %5d good, %4d groupless\n", surveys.size(), orphanSurveys);
        System.out.printf("#QuestionGroups:  %5d good, %4d surveyless\n", qgToSurvey.size(), orphanGroups);
        System.out.printf("#Questions:       %5d good, %4d group-or-surveyless, %4d unreachable\n", goodQuestions, orphanQuestions, unreachableQuestions);
        System.out.printf("#QuestionOptions: %5d good, %4d questionless\n", goodOptions, orphanOptions++);
        System.out.printf("#Translations:    %5d good, %4d lost\n", goodTranslations, orphanTranslations);

    }

    private void processGroups(DatastoreService ds) {

        System.out.println("#Processing Question Groups");

        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);
        List<Key> qgsToKill = new ArrayList<Key>();

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long qgId = g.getKey().getId();
            Long surveyId = (Long) g.getProperty("surveyId");
            String qgName = (String) g.getProperty("name");
            if (surveyId == null) {
                System.out.printf("#ERR group %d '%s' is not in a survey\n", qgId, qgName);
                orphanGroups++;
                qgsToKill.add(g.getKey());
            } else if (!surveys.containsKey(surveyId)) {
                System.out.printf("#ERR group %d '%s' is in nonexistent survey %d\n",
                        qgId, qgName, surveyId);
                orphanGroups++;
                qgsToKill.add(g.getKey());
            } else {
                qgToSurvey.put(qgId, surveyId); //ok to have questions in
            }
            //delete orphan groups
        }
        if (deleteOrphans) {
            System.out.printf("#Deleting %d QuestionGroups\n", qgsToKill.size());
            batchDelete(ds, qgsToKill);
        }
    }

    private void processAllSurveys(DatastoreService ds) {

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
                surveys.put(surveyId,surveyName); //ok to have questions in
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

            if (questionSurvey == null || questionGroup == null || questionGroupSurvey == null) { // in no survey, group or a nonexistent group; hopelessly lost
                System.out.printf("#ERR: Question %d '%s',survey %d, group %d\n",
                        questionId, questionText, questionSurvey, questionGroup);
                orphanQuestions++;
                if (deleteOrphans){
                    System.out.println(q.toString());//for posterity
                    q.setProperty("surveyId", questionGroupSurvey);
                    questionsToKill.add(q.getKey());
                }
            } else { // check for wrong survey/qg
                qToSurvey.put(questionId, questionSurvey); //ok parent for options
                qToQg.put(questionId, questionGroup);
                if (!questionSurvey.equals(questionGroupSurvey)) {
                    System.out.printf("#ERR: Question %d '%s' in survey %d, but group %d is in survey %d\n",
                            questionId, questionText, questionSurvey, questionGroup, questionGroupSurvey);
                    if (fixSurveyPointers){
                        System.out.println(q.toString());//for posterity
                        q.setProperty("surveyId", questionGroupSurvey);
                        questionsToFix.add(q);
                    }
                    unreachableQuestions++;
                } else {
                    goodQuestions++;
                }
            }
        }
        if (fixSurveyPointers) {
            System.out.printf("#Fixing %d Questions\n",questionsToFix.size());
            batchSaveEntities(ds, questionsToFix);
        }
        if (deleteOrphans) {
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
                orphanOptions++;
                if (deleteOrphans) {
                    optionsToKill.add(option.getKey());
                    System.out.println(option.toString());//for posterity
                }
            } else { // check for bad question
                if (!qToSurvey.containsKey(questionId)) {
                    System.out.printf(
                            "#ERR: Option %d '%s' is in nonexistent question %d\n",
                            optionId, optionText, questionId);
                    orphanOptions++;
                    if (deleteOrphans) {
                        optionsToKill.add(option.getKey());
                        System.out.println(option.toString());//for posterity
                    }
                } else {
                    goodOptions++;
                }
            }
        }
        if (deleteOrphans) {
            System.out.printf("#Deleting %d Options\n",optionsToKill.size());
            batchDelete(ds, optionsToKill);
        }
    }

    private void processTranslations(DatastoreService ds) {

        System.out.println("#Processing Translations");

        final Query tran_q = new Query("Translation");
        final PreparedQuery tran_pq = ds.prepare(tran_q);
        List<Key> transToKill = new ArrayList<Key>();

        for (Entity t : tran_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long tranId = t.getKey().getId();
            Long surveyId = (Long) t.getProperty("surveyId");
            Long parentId = (Long) t.getProperty("parentId");
            Long qgId = (Long) t.getProperty("questionGroupId");
            String parentType = (String) t.getProperty("parentType");
            if (surveyId == null) {
                System.out.printf("#ERR translation %d '%s' is not in a survey\n", tranId, parentType);
                orphanTranslations++;
                transToKill.add(t.getKey());
            } else if (!surveys.containsKey(surveyId)) {
                System.out.printf("#ERR translation %d '%s' is not in a good survey\n", tranId, parentType);
                orphanTranslations++;
                transToKill.add(t.getKey());
            } else if (parentId == null) {
                System.out.printf("#ERR translation %d '%s' has no parent\n", tranId, parentType);
                orphanTranslations++;
                transToKill.add(t.getKey());
            } else if (qgId != null && !qgToSurvey.containsKey(qgId)) {
            	System.out.printf("#ERR translation %d '%s' is not in a good question group\n", tranId, parentType);
            	orphanTranslations++;
            	transToKill.add(t.getKey());
            } else if (qgId != null && !surveyId.equals(qgToSurvey.get(qgId))) {
            	System.out.printf("#ERR translation %d '%s' %d is not in a question group of the right survey. My survey %d, qg's survey %d\n",
            			tranId,
            			parentType,
            			parentId,
            			surveyId,
            			qgToSurvey.get(qgId));
            	orphanTranslations++;
            	transToKill.add(t.getKey());
            } else if (parentType.equals("QUESTION_TEXT") && !qToSurvey.containsKey(parentId)) {
            	System.out.printf("#ERR translation %d '%s' is in a nonexistent question\n", tranId, parentType);
            	orphanTranslations++;
            	transToKill.add(t.getKey());
            } else if (parentType.equals("QUESTION_TEXT") && !qgId.equals(qToQg.get(parentId))) {
            	System.out.printf("#ERR translation %d '%s' is in a question with a different question group\n", tranId, parentType);
            	orphanTranslations++;
            	transToKill.add(t.getKey());
            } else {
                goodTranslations++;
            }
        }
        if (deleteOrphans) {
            System.out.printf("#Deleting %d Translations\n", transToKill.size());
            batchDelete(ds, transToKill);
        }
    }
}

