/*
 *  Copyright (C) 2017,2019 Stichting Akvo (Akvo Foundation)
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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/*
 */
public class PrintTreeStructure implements Process {

    private Map<Long, String> sgName = new HashMap<>();
    private Map<Long, String> sgType = new HashMap<>();
    private Map<Long, Long> sgParents = new HashMap<>();

    private Map<Long, String> surveyNames = new HashMap<>();
    private Map<Long, Long> surveyParents = new HashMap<>();
    private Map<Long, List<Long>> surveyToGroups = new HashMap<>();

    private Map<Long, String> qNames = new HashMap<>();
    private Map<Long, String> qTypes = new HashMap<>();
    private Map<Long, Long> qParents = new HashMap<>();
    private Map<Long, Long> qToSurvey = new HashMap<>();
    private Map<Long, Long> qOrder = new HashMap<>();
    private Map<Long, Boolean> qMandatory = new HashMap<>();

    private Map<Long, String> qgNames = new HashMap<>();
    private Map<Long, Long> qgOrder= new HashMap<>();
    private Map<Long, Long> qgParents = new HashMap<>();
    private Map<Long, List<Long>> qgToQuestions = new HashMap<>();

    private Map<Long, String> oNames = new HashMap<>();
    private Map<Long, Long> oParents = new HashMap<>();

    private boolean html = false;
    private boolean showQuestions = false;
    private boolean showOptions = false;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.printf("#Arguments: --questions  and --options to descend to that depth.\n");
        for (int i = 0; i < args.length; i++) {
            //System.out.printf("#Argument %d: %s\n", i, args[i]);
            if (args[i].equalsIgnoreCase("--html")) {
                html = true;
            }
            if (args[i].equalsIgnoreCase("--questions")) {
                showQuestions = true;
            }
            if (args[i].equalsIgnoreCase("--options")) {
                showOptions = true;
            }
        }

        fetchSurveyGroups(ds);
        fetchSurveys(ds);
        if (showQuestions) {
            fetchQuestionGroups(ds);
            fetchQuestions(ds);
            if (showOptions) {
                fetchOptions(ds);
            }
        }

//        System.out.printf("/ (root)\n");
        drawSurveyGroupsIn(0L, 0);

    }

    private void drawSurveyGroupsIn(Long parent, int indent) {
        for (Long sg : sgParents.keySet()) {
            if (sgParents.get(sg).equals(parent)) {
                String s="";
                for (int i = 0; i<indent; i++) {
                    s+="  ";
                }
                if (sgType.get(sg).equals("PROJECT")) {
                    System.out.printf("%s* %s [%d]\n", s, sgName.get(sg), sg);
                    drawFormsIn(sg, indent+1);
                } else {
                    System.out.printf("%s/ %s [%d]\n", s, sgName.get(sg), sg);
                    drawSurveyGroupsIn(sg, indent+1);
                }
            }
        }
    }

    private void drawFormsIn(Long parent, int indent) {
        for (Long survey : surveyParents.keySet()) {
            if (surveyParents.get(survey).equals(parent)) {
                String s="";
                for (int i = 0; i<indent; i++) {
                    s+="  ";
                }
                System.out.printf("%s# %s [%d]\n", s, surveyNames.get(survey), survey);
                if (showQuestions) {
                    drawGroupsIn(survey, indent+1);
                }
            }
        }
    }

    private void drawGroupsIn(Long parent, int indent) {
        for (Long qg : qgParents.keySet()) {
            if (qgParents.get(qg).equals(parent)) {
                String s="";
                for (int i = 0; i<indent; i++) {
                    s+="  ";
                }
                System.out.printf("%s@ %s %s [%d]\n", s, qgOrder.get(qg), qgNames.get(qg), qg);
                drawQuestionsIn(qg, indent+1);
            }
        }
    }

    private void drawQuestionsIn(Long parent, int indent) {
        for (Long q : qParents.keySet()) {
            String m = (qMandatory.get(q).equals(Boolean.TRUE)) ? "*":"";

            if (qParents.get(q).equals(parent)) {
                String s = "";
                for (int i = 0; i<indent; i++) {
                    s += "  ";
                }
                if (qTypes.get(q).equals("OPTION")) {
                    System.out.printf("%s?OPTION%s %s [%d]\n", s, m, qNames.get(q), q);
                    if (showOptions) {
                        drawOptionsIn(q, indent+1);
                    }
                } else {
                    System.out.printf("%s?%s%s %s [%d]\n", s, qTypes.get(q), m, qNames.get(q), q);
                }
            }
        }
    }

    private void drawOptionsIn(Long parent, int indent) {
        for (Long o : oParents.keySet()) {
            if (oParents.get(o).equals(parent)) {
                String s="";
                for (int i = 0; i<indent; i++) {
                    s+="  ";
                }
                System.out.printf("%s+ %s [%d]\n", s, oNames.get(o), o);
            }
        }
    }

    private void fetchQuestionGroups(DatastoreService ds) {

        System.out.println("#Fetching Question Groups");

        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroupId = g.getKey().getId();
            Long questionGroupSurvey = (Long) g.getProperty("surveyId");
            Long order = (Long) g.getProperty("order");
            String questionGroupName = (String) g.getProperty("name");
            if (questionGroupSurvey == null) {
                System.out.printf("#ERR group [%d] '%s' is not in a survey\n",
                        questionGroupId, questionGroupName);
            } else if (!surveyNames.containsKey(questionGroupSurvey)) {
                System.out.printf("#ERR group [%d] '%s' in nonexistent survey %d\n",
                        questionGroupId, questionGroupName, questionGroupSurvey);
            } else {
                qgNames.put(questionGroupId, questionGroupName);
                qgParents.put(questionGroupId, questionGroupSurvey); //ok to have questions in
                surveyToGroups.get(questionGroupSurvey).add(questionGroupId);
                qgOrder.put(questionGroupId, order);
                qgToQuestions.put(questionGroupId, new ArrayList<Long>() );//empty list to collect questions in
            }
        }
    }

    private void fetchSurveyGroups(DatastoreService ds) {

        System.out.println("#Fetching Survey Groups");

        final Query q = new Query("SurveyGroup");
        final PreparedQuery pq = ds.prepare(q);

        for (Entity g : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyGroupId = g.getKey().getId();
            Long parentId = (Long) g.getProperty("parentId");
            String type = (String) g.getProperty("projectType");
            String name = (String) g.getProperty("name");
            if (parentId == null) {
            } else {
                sgParents.put(surveyGroupId, parentId); //ok to have questions in
                sgName.put(surveyGroupId, name);
                sgType.put(surveyGroupId, type);
            }
        }
    }

    private void fetchSurveys(DatastoreService ds) {

        System.out.println("#Processing Surveys");

        final Query survey_q = new Query("Survey");
        final PreparedQuery survey_pq = ds.prepare(survey_q);

        for (Entity s : survey_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long surveyId = s.getKey().getId();
            String surveyName = (String) s.getProperty("name");
            Long surveyGroup = (Long) s.getProperty("surveyGroupId");
            if (surveyGroup == null) {
            } else {
                surveyNames.put(surveyId,surveyName);
                surveyParents.put(surveyId, surveyGroup);
                surveyToGroups.put(surveyId, new ArrayList<Long>() );
            }
        }
    }

    private void fetchQuestions(DatastoreService ds) {
        System.out.println("#Fetching Questions");

        final Query qq = new Query("Question");
        final PreparedQuery qpq = ds.prepare(qq);

        for (Entity q : qpq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionId = q.getKey().getId();
            Long questionSurvey = (Long) q.getProperty("surveyId");
            Long questionGroup = (Long) q.getProperty("questionGroupId");
            Long order = (Long) q.getProperty("order");
            String questionText = (String) q.getProperty("text");
            String qType = (String) q.getProperty("type");
            Long questionGroupSurvey = (Long) qgParents.get(questionGroup);
            Boolean mandatory = (Boolean) q.getProperty("mandatoryFlag");

            if (questionGroup == null || questionGroupSurvey == null) { // in no group or a nonexistent group; hopelessly lost
                System.out.printf("#ERR: Question [%d] '%s',survey %d, group %d\n",
                        questionId, questionText, questionSurvey, questionGroup);
            } else { // check for wrong survey/qg
                qToSurvey.put(questionId, questionSurvey); //ok parent for options
                qParents.put(questionId, questionGroup);
                qNames.put(questionId, questionText);
                qTypes.put(questionId, qType);
                qOrder.put(questionId, order);
                qMandatory.put(questionId, mandatory);
                if (!questionGroupSurvey.equals(questionSurvey)) {
                    System.out.printf("#ERR: Question [%d] '%s' in survey %d, but group %d is in survey %d\n",
                            questionId, questionText, questionSurvey, questionGroup, questionGroupSurvey);
                } else {
                    qgToQuestions.get(questionGroup).add(questionId);

                }
            }
        }
    }

    private void fetchOptions(DatastoreService ds) {
        System.out.println("#Feching Options");

        final Query oq = new Query("QuestionOption");
        final PreparedQuery opq = ds.prepare(oq);

        for (Entity option : opq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long optionId = option.getKey().getId();
            Long questionId = (Long) option.getProperty("questionId");
            String optionText = (String) option.getProperty("text");

            if (questionId == null) { // check for no question
                System.out.printf("#ERR: Option %d '%s', not in a question\n", optionId, optionText);
            } else {
                oParents.put(optionId, questionId);
                oNames.put(optionId, optionText);
            }
        }
    }
}
