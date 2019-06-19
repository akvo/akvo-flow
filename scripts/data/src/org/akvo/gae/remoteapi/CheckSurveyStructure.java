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
import java.util.TreeMap;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
 * - Checks that all folders, surveys, forms, groups, questions and options are consistent
 */
public class CheckSurveyStructure implements Process {

    private int orphanFolders = 0, orphanSurveys = 0, orphanForms = 0, orphanGroups = 0, orphanQuestions = 0, unreachableQuestions = 0, orphanOptions = 0;
    private int goodQuestions = 0, goodOptions = 0;
    private Map<Long, String> folders = new HashMap<>();
    private Map<Long, String> surveys = new HashMap<>();
    private Map<Long, String> forms = new HashMap<>();
    private Map<Long, Long> qToSurvey = new HashMap<>();
    private Map<Long, Long> qgToSurvey = new HashMap<>();

    private List<Key> orphans = new ArrayList<>();

    private boolean fixSurveyPointers = false; // Make question survey pointer match the group's
    private boolean deleteOrphans = false;
    private boolean showDetails = false;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: --fix to correct form pointers of unreachable questions, --gc to delete orphaned entites, --details for complete entity output.\n");
        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--fix")) {
                fixSurveyPointers = true;
            }
            if (args[i].equalsIgnoreCase("--gc")) {
                deleteOrphans = true;
                showDetails = true;
            }
            if (args[i].equalsIgnoreCase("--details")) {
                showDetails = true;
            }
        }

        processFoldersAndSurveys(ds);
        processForms(ds);
        processGroups(ds);
        processQuestions(ds);
        processOptions(ds);

        System.out.printf("#Folders:         %5d good, %4d orphans\n", folders.size(), orphanFolders);
        System.out.printf("#Surveys:         %5d good, %4d orphans\n", surveys.size(), orphanSurveys);
        System.out.printf("#Forms:           %5d good, %4d orphans\n", forms.size(),   orphanForms);
        System.out.printf("#QuestionGroups:  %5d good, %4d orphans\n", qgToSurvey.size(), orphanGroups);
        System.out.printf("#Questions:       %5d good, %4d orphans, %4d unreachable\n", goodQuestions, orphanQuestions, unreachableQuestions);
        System.out.printf("#QuestionOptions: %5d good, %4d orphans\n", goodOptions, orphanOptions++);

        if (deleteOrphans) {
            System.out.printf("#Deleting %d orphans\n", orphans.size());
            batchDelete(ds, orphans);
        } else {
            System.out.printf("#Not deleting %d orphans\n", orphans.size());
        }

    }

    //How many steps lead to the root (parent=0)?
    private int rootDepth(Long id, Map<Long, Entity> folders) {
        int depth = 0;
        while (depth < 100) { //Max we believe in; could be in a loop
            Entity e = folders.get(id);
            if (e == null) return -1; //Fail, broken
            Long parentId = (Long) e.getProperty("parentId"); //0 means root folder
            if (parentId == null) return -1; //Fail
            if (parentId == 0L) return depth;
            depth++;
            id = parentId;
        }
        return -1; //Fail, too far
    }

    private void processFoldersAndSurveys(DatastoreService ds) {

        System.out.println("#Processing Folders and surveys");
        Map<Long, Entity> tmpfolders = new HashMap<>();
        Map<Long, Entity> tmpsurveys = new HashMap<>();

        final Query survey_q = new Query("SurveyGroup");
        final PreparedQuery survey_pq = ds.prepare(survey_q);

        for (Entity fs : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long id = fs.getKey().getId();
            String type = (String) fs.getProperty("projectType");
            String name = (String) fs.getProperty("name");
            if ("PROJECT_FOLDER".equals(type)) {
                tmpfolders.put(id, fs);
            } else if ("PROJECT".equals(type)) {
                tmpsurveys.put(id, fs);
            } else { //Bad type; leave it alone. But note that dependent items will be treated as orphans!
                System.out.printf("#ERR folder/survey %d '%s' has bad type '%s'\n", id, name, type);
                /*TODO: maybe remove it? If so:
                orphans.add(fs.getKey());
                orphanFolders++;
                */
                if (showDetails) {
                    System.out.println(fs.toString());//for posterity
                }
            }
        }
        //Now verify tree
        for (Entity fs : tmpfolders.values()) {
            Long id = fs.getKey().getId();
            String name = (String) fs.getProperty("name");
            if (rootDepth(id, tmpfolders) < 0) {
                Long parentId = (Long) fs.getProperty("parentId"); //0 means root folder
                System.out.printf("#ERR folder %d '%s' is lost; not reachable from root. (Parent is %d)\n", id, name, parentId);
                orphans.add(fs.getKey());
                orphanFolders++;
                if (showDetails) {
                    System.out.println(fs.toString());//for posterity
                }
            } else {
                folders.put(id, name);
            }
        }

        for (Entity fs : tmpsurveys.values()) {
            String name = (String) fs.getProperty("name");
            Long id = fs.getKey().getId();
            Long parentId = (Long) fs.getProperty("parentId");
            if (parentId == null || parentId == 0L) { //0 means root folder; deprecated, legacy only
                System.out.printf("#WARN survey %d '%s' is in root folder\n", id, name);
                surveys.put(id, name);
                continue;
            }
            Entity parent = tmpfolders.get(parentId);
            if (parent == null) {
                System.out.printf("#ERR survey %d '%s' nonexistent parent %d\n", id, name, parentId);
                orphans.add(fs.getKey());
                orphanSurveys++;
                if (showDetails) {
                    System.out.println(fs.toString());//for posterity
                }
            } else {
                surveys.put(id, name);
            }
        }

    }


    private void processForms(DatastoreService ds) {

        System.out.println("#Processing Forms");

        final Query survey_q = new Query("Survey");
        final PreparedQuery survey_pq = ds.prepare(survey_q);

        for (Entity form : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long id = form.getKey().getId();
            String name = (String) form.getProperty("name");
            Long parentId = (Long) form.getProperty("surveyGroupId");
            if (parentId == null || parentId == 0L) {
                System.out.printf("#ERR form %d '%s' is not in a survey (%d instances)\n",
                        id, name, formInstanceIdsForForm(ds, id).size());
                orphanForms++;
                orphans.add(form.getKey());
                if (showDetails) {
                    System.out.println(form.toString());//for posterity
                }
                continue;
            }
            String parent = surveys.get(parentId);
            if (parent == null) {
                System.out.printf("#ERR form %d '%s' in nonexistent survey %d (%d instances)\n",
                        id, name, parentId, formInstanceIdsForForm(ds, id).size());
                orphans.add(form.getKey());
                orphanSurveys++;
                if (showDetails) {
                    System.out.println(form.toString());//for posterity
                }
            } else {
                forms.put(id, name); //ok to have question groups in
            }
        }
    }


    private void processGroups(DatastoreService ds) {

        System.out.println("#Processing Question Groups");

        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroupId = g.getKey().getId();
            Long formId = (Long) g.getProperty("surveyId");
            String questionGroupName = (String) g.getProperty("name");
            if (formId == null) {
                System.out.printf("#ERR group %d '%s' is not in a form\n",
                        questionGroupId, questionGroupName);
                orphans.add(g.getKey());
                orphanGroups++;
                if (showDetails) {
                    System.out.println(g.toString());//for posterity
                }
            } else if (!forms.containsKey(formId)) {
                System.out.printf("#ERR group %d '%s' in nonexistent form %d\n",
                        questionGroupId, questionGroupName, formId);
                orphans.add(g.getKey());
                orphanGroups++;
                if (showDetails) {
                    System.out.println(g.toString());//for posterity
                }
            } else {
                qgToSurvey.put(questionGroupId, formId); //ok to have questions in
            }
        }
    }


    private void processQuestions(DatastoreService ds) {
        System.out.println("#Processing Questions");

        final Query qq = new Query("Question");
        final PreparedQuery qpq = ds.prepare(qq);
        List<Entity> questionsToFix = new ArrayList<Entity>();
        List<Key> orphanedQuestions = new ArrayList<Key>();

        for (Entity q : qpq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionId = q.getKey().getId();
            Long questionSurvey = (Long) q.getProperty("surveyId");
            Long questionGroup = (Long) q.getProperty("questionGroupId");
            String questionText = (String) q.getProperty("text");
            Long questionGroupSurvey = qgToSurvey.get(questionGroup);

            if (questionSurvey == null || questionGroup == null || questionGroupSurvey == null) { // in no survey, no group or a nonexistent group; hopelessly lost
                System.out.printf("#ERR: Question %d '%s',form %d, group %d  (%d answers)\n",
                        questionId, questionText, questionSurvey, questionGroup, qasIdsForQuestion(ds, questionId).size());
                orphanedQuestions.add(q.getKey());
                orphanQuestions++;
                if (showDetails) {
                    System.out.println(q.toString());
                }
            } else { // check for wrong survey/qg
                qToSurvey.put(questionId, questionSurvey); //ok parent for options
                if (!questionSurvey.equals(questionGroupSurvey)) {
                    System.out.printf("#ERR: Question %d '%s' in survey %d, but group %d is in survey %d\n",
                            questionId, questionText, questionSurvey, questionGroup, questionGroupSurvey);
                    if (showDetails) {
                        System.out.println(q.toString());
                    }
                    if (fixSurveyPointers){
                        q.setProperty("surveyId", questionGroupSurvey);
                        questionsToFix.add(q);
                    }
                    unreachableQuestions++;
                } else {
                    goodQuestions++;
                }
            }
        }
        System.out.printf("#Deleting %d Questions\n",orphanedQuestions.size());
        orphans.addAll(orphanedQuestions);
        if (fixSurveyPointers) {
            System.out.printf("#Fixing %d Questions\n",questionsToFix.size());
            batchSaveEntities(ds, questionsToFix);
        }
    }

    private void processOptions(DatastoreService ds) {
        System.out.println("#Processing Options");

        final Query oq = new Query("QuestionOption");
        final PreparedQuery opq = ds.prepare(oq);
        List<Key> orphanedOptions = new ArrayList<Key>();

        for (Entity option : opq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long optionId = option.getKey().getId();
            Long questionId = (Long) option.getProperty("questionId");
            String optionText = (String) option.getProperty("text");

            if (questionId == null) { // check for no question
                System.out.printf("#ERR: Option %d '%s', not in a question\n", optionId, optionText);
                orphanOptions++;
                orphanedOptions.add(option.getKey());
                if (showDetails) {
                    System.out.println(option.toString());//for posterity
                }
            } else { // check for bad question
                if (!qToSurvey.containsKey(questionId)) {
                    System.out.printf(
                            "#ERR: Option %d '%s' is in nonexistent question %d\n",
                            optionId, optionText, questionId);
                    orphanOptions++;
                    orphanedOptions.add(option.getKey());
                    if (showDetails) {
                        System.out.println(option.toString());//for posterity
                    }
                } else {
                    goodOptions++;
                }
            }
        }
        System.out.printf("#Deleting %d Options\n",orphanedOptions.size());
        orphans.addAll(orphanedOptions);
    }

    //Todo: opt for keys only
    private List<Long> formInstanceIdsForForm(DatastoreService ds, Long formId) {
        final Query qasq = new Query("SurveyInstance")
                .setFilter(new Query.FilterPredicate("surveyId", FilterOperator.EQUAL, formId));
        final PreparedQuery pq = ds.prepare(qasq);
        List<Long> ids = new ArrayList<>();
        for (Entity fi : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            ids.add(fi.getKey().getId());
        }
        return ids;
    }

    //Todo: opt for keys only
    private List<Long> qasIdsForQuestion(DatastoreService ds, Long qId) {
        final Query qasq = new Query("QuestionAnswerStore")
                .setFilter(new Query.FilterPredicate("questionID", FilterOperator.EQUAL, qId.toString())); //Note uppercase D and type String
        final PreparedQuery pq = ds.prepare(qasq);
        List<Long> answers = new ArrayList<>();
        for (Entity qa : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            answers.add(qa.getKey().getId());
        }
        return answers;
    }


}
