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

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: --fix to correct form pointers of unreachable questions, --gc to delete orphaned entites.\n");
        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--fix")) {
                fixSurveyPointers = true;
            }
            if (args[i].equalsIgnoreCase("--gc")) {
                deleteOrphans = true;
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
            if (parentId == 0) return depth;
            depth++;
            id = parentId;
        }
        return -1; //Fail, too long
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
            } else { //Bad type
                System.out.printf("#ERR folder/survey %d '%s' has bad type '%s'\n", id, name, type);
            }
        }
        //Now verify tree
        for (Entity fs : tmpfolders.values()) {
            Long id = fs.getKey().getId();
            String name = (String) fs.getProperty("name");
            if (rootDepth(id, tmpfolders) < 0) {
                Long parentId = (Long) fs.getProperty("parentId"); //0 means root folder
                System.out.printf("#ERR folder %d '%s' not reachable from root. (Parent is %d)\n", id, name, parentId);
                orphans.add(fs.getKey());
                orphanFolders++;
            } else {
                folders.put(id, name);
            }
        }
        //TODO recursively remove orphan folders and surveys (as suitable parents for forms) will make this one-pass
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
                System.out.printf("#ERR form %d '%s' is not in a survey\n", id, name);
                orphanForms++;
                orphans.add(form.getKey());
                continue;
            }
            String parent = surveys.get(parentId);
            if (parent == null) {
                System.out.printf("#ERR form %d '%s' in nonexistent survey %d\n", id, name, parentId);
                orphans.add(form.getKey());
                orphanSurveys++;
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
            } else if (!forms.containsKey(formId)) {
                System.out.printf("#ERR group %d '%s' in nonexistent form %d\n",
                        questionGroupId, questionGroupName, formId);
                orphans.add(g.getKey());
                orphanGroups++;
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

            if (questionSurvey == null || questionGroup == null || questionGroupSurvey == null) { // in no survey, group or a nonexistent group; hopelessly lost
                System.out.printf("#ERR: Question %d '%s',survey %d, group %d\n",
                        questionId, questionText, questionSurvey, questionGroup);
                orphanedQuestions.add(q.getKey());
                orphanQuestions++;
                if (deleteOrphans){
                    System.out.println(q.toString());//log it for posterity
                    q.setProperty("surveyId", questionGroupSurvey);
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
                if (deleteOrphans) {
                    System.out.println(option.toString());//for posterity
                }
            } else { // check for bad question
                if (!qToSurvey.containsKey(questionId)) {
                    System.out.printf(
                            "#ERR: Option %d '%s' is in nonexistent question %d\n",
                            optionId, optionText, questionId);
                    orphanOptions++;
                    orphanedOptions.add(option.getKey());
                    if (deleteOrphans) {
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
}
