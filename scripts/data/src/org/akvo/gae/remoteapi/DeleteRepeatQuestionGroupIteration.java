/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DeleteRepeatQuestionGroupIteration implements Process {

    private static long NON_EXISTENT_ID = -1L;

    private Long surveyInstanceId;

    private Long formId;

    private String questionGroupName;

    private List<Long> iterationsToDelete;

    private boolean deleteIterations;

    private boolean updateIterations;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        System.out.println("##############################################################");
        System.out.printf(
                        "#      Arguments: survey instance id, current question group name between quotes, %n" +
                        "#      comma-separated list of iterations (as they appear in data  %n" +
                        "#      cleaning report i.e. starting at number 1...) to delete %n" +
                        "#      d to confirm deletion of iterations %n");
        System.out.printf("##############################################################%n%n%n");

        parseArgs(args, ds);

        List<Long> repeatGroupQuestionIds = retrieveRepeatGroupQuestionIds(ds);
        if (repeatGroupQuestionIds.isEmpty()) {
            System.out.println("No questions found for group: *" + questionGroupName + "*");
            System.exit(0);
        }

        List<Entity> responses = retrieveResponses(ds, this.surveyInstanceId, repeatGroupQuestionIds);

        if (deleteIterations) {
            deleteRepeatGroupIterations(ds, responses, this.iterationsToDelete);
        } else {
            System.out.println("No delete request made");
        }

        if (updateIterations) {
            updateResponseIterations(ds, responses, this.iterationsToDelete);
        } else {
            System.out.println("No iterations to update");
        }
    }

    private void parseArgs(String[] args, DatastoreService ds) {
        Long surveyInstanceId = parseSurveyInstanceId(args);
        if (surveyInstanceId.equals(-1L)) {
            System.out.println("No survey instance id found. Exiting");
            System.exit(1);
        } else {
            this.surveyInstanceId = surveyInstanceId;
        }

        this.formId = retrieveFormId(surveyInstanceId, ds);

        String questionGroupName = parseQuestionGroupName(args);
        if (questionGroupName == null || questionGroupName.isEmpty()) {
            System.out.println("No question group name specified. Exiting");
            System.exit(1);
        } else {
            this.questionGroupName = questionGroupName;
        }

        List<Long> iterationsToDelete = parseIterations(args);
        if (iterationsToDelete.isEmpty()) {
            System.out.println("No iterations to delete. Exiting");
            System.exit(0);
        } else {
            this.iterationsToDelete = iterationsToDelete;
        }

        this.deleteIterations = confirmDeletionRequest(args);

        this.updateIterations = confirmIterationUpdate(args);
    }

    private Long parseSurveyInstanceId(String[] args) {
        Long instanceId = null;
        try {
            if (args.length > 0) {
                instanceId = Long.parseLong(args[0]);
            }
        } catch (NumberFormatException e) {
            return NON_EXISTENT_ID;
        }

        return instanceId;
    }

    private Long retrieveFormId(Long surveyInstanceId, DatastoreService ds) {
        Long formId = null;
        try {
            Entity surveyInstance = ds.get(KeyFactory.createKey("SurveyInstance", surveyInstanceId));

            formId = (Long) surveyInstance.getProperty("surveyId");
        } catch (EntityNotFoundException e) {
            System.out.println("Failed to find the SurveyInstance: " + surveyInstanceId);
        }
        return formId;
    }

    private String parseQuestionGroupName(String[] args) {
        if (args.length > 1) {
            return args[1];
        }
        return null;
    }

    private List<Long> parseIterations(String[] args) {
        List<Long> iterationsToDelete = new ArrayList<>();
        if (args.length > 2) {
            String[] iterations = args[2].split(",");
            for (int i = 0; i < iterations.length; i++) {
                try {
                    Long iter = Long.parseLong(iterations[i]);
                    iterationsToDelete.add(--iter);  // we decrement in order to translate iterations as shown in the
                                                    // spreadsheet data cleaning report to the representation in datastore
                } catch (NumberFormatException e) {
                    //
                }

            }
        }

        return iterationsToDelete;
    }

    private boolean confirmDeletionRequest(String[] args) {
        return args.length > 3 && "d".equals(args[3]);
    }

    private boolean confirmIterationUpdate(String[] args) {
        return args.length > 3 && "u".equals(args[3]);
    }

    private List<Long> retrieveRepeatGroupQuestionIds(DatastoreService ds) {
        List<Long> ids = new ArrayList<>();
        Entity questionGroup = retrieveQuestionGroup(ds, this.questionGroupName);

        Filter questionsFilter = new Query.FilterPredicate("questionGroupId", Query.FilterOperator.EQUAL, questionGroup.getKey().getId());
        Query q = new Query("Question");
        q.setFilter(questionsFilter).setKeysOnly();
        PreparedQuery pq = ds.prepare(q);

        for (Entity question : pq.asIterable(FetchOptions.Builder.withDefaults())) {
            ids.add(question.getKey().getId());
        }

        return ids;
    }

    private Entity retrieveQuestionGroup(DatastoreService ds, String name) {
        Filter questionGroupFilter = new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, name);
        Filter formIdFilter = new Query.FilterPredicate("surveyId", Query.FilterOperator.EQUAL, this.formId);

        Filter combinedFilter = Query.CompositeFilterOperator.and(formIdFilter, questionGroupFilter);

        Query q = new Query("QuestionGroup");
        q.setFilter(combinedFilter).setKeysOnly();
        PreparedQuery pq = ds.prepare(q);

        return pq.asSingleEntity();
    }

    private List<Entity> retrieveResponses(DatastoreService ds, Long surveyInstanceId, List<Long> repeatingQuestionIds) {
        Filter instanceFilter = new Query.FilterPredicate("surveyInstanceId", Query.FilterOperator.EQUAL, surveyInstanceId);
        Filter questionsFilter = buildQuestionFilter(repeatingQuestionIds);

        Filter combinedFilters = Query.CompositeFilterOperator.and(instanceFilter, questionsFilter);

        Query q = new Query("QuestionAnswerStore");
        q.setFilter(combinedFilters);
        PreparedQuery pq = ds.prepare(q);

        List<Entity> responses = new ArrayList<>();
        for (Entity response : pq.asIterable(FetchOptions.Builder.withDefaults())){
            responses.add(response);
        }

        System.out.println("Found " + responses.size() + " responses ");

        return responses;
    }

    private Filter buildQuestionFilter(List<Long> repeatingQuestionIds) {
        Filter questionsFilter = null;
        if (repeatingQuestionIds.size() > 1) {
            List<Filter> questionsSubFilterList = new ArrayList<>();
            for (Long questionId : repeatingQuestionIds) {
                questionsSubFilterList.add(new Query.FilterPredicate("questionID", Query.FilterOperator.EQUAL, questionId.toString()));
            }
            questionsFilter = Query.CompositeFilterOperator.or(questionsSubFilterList);
        } else {
            questionsFilter = new Query.FilterPredicate("questionID", Query.FilterOperator.EQUAL, repeatingQuestionIds.get(0).toString());
        }
        return questionsFilter;
    }

    private Filter  buildIterationsFilter(List<Long> iterationsToDelete) {
        Filter iterationsFilter = null;
        if (iterationsToDelete.size() > 1) {
            List<Filter> iterationFiltersList = new ArrayList<>();
            for (Long iteration : iterationsToDelete) {
                Filter f = new Query.FilterPredicate("iteration", Query.FilterOperator.EQUAL, iteration);
                iterationFiltersList.add(f);
            }
            iterationsFilter = Query.CompositeFilterOperator.or(iterationFiltersList);
        } else {
            iterationsFilter = new Query.FilterPredicate("iteration", Query.FilterOperator.EQUAL, iterationsToDelete.get(0));
        }

        return iterationsFilter;
    }

    private void deleteRepeatGroupIterations(DatastoreService ds, List<Entity> responses, List<Long> iterationsToDelete) {
        List<Entity> responsesToDelete = responses.stream()
                .filter(response -> iterationsToDelete.contains(response.getProperty("iteration")))
                .collect(Collectors.toList());

        System.out.println("Deleting " + responsesToDelete.size() + " responses.");

        List<Key> responseKeysToDelete = new ArrayList<>();
        for (Entity response : responsesToDelete) {
            responseKeysToDelete.add(response.getKey());
        }

        DataUtils.batchDelete(ds, responseKeysToDelete);
    }

    private void updateResponseIterations(DatastoreService ds, List<Entity> responses, List<Long> iterationsDeleted) {
        List<Entity> responsesToUpdate = filterResponsesToUpdate(responses, iterationsDeleted);

        resetIterationIds(ds, responsesToUpdate, getLowestIterationNumber(iterationsToDelete));
    }

    private List<Entity> filterResponsesToUpdate(List<Entity> responses, List<Long> iterationsDeleted) {
        return responses.stream()
                .filter(response -> !iterationsDeleted.contains(response.getProperty("iteration")))
                .collect(Collectors.toList());
    }

    private void resetIterationIds(DatastoreService ds, List<Entity> responsesToUpdate, Long lowestDeletedIteration) {
        System.out.println("Updating iteration ids for " + responsesToUpdate.size() + " responses");

        SortedMap<Integer, List<Entity>> sortedIterationMap = new TreeMap<>();

        responsesToUpdate.stream().forEach(response -> addResponseToIterationMap(sortedIterationMap, response));

        List<Entity> updatedResponses = new ArrayList<>();
        int newIteration = 0;
        for (List<Entity> responses : sortedIterationMap.values()) {
            for (Entity response : responses) {
                response.setProperty("iteration", newIteration);
                updatedResponses.add(response);
            }
            newIteration++;
        }

        DataUtils.batchSaveEntities(ds, updatedResponses);
    }

    private void addResponseToIterationMap(SortedMap<Integer, List<Entity>> sortedIterationMap, Entity response) {
        int iteration = ((Long) response.getProperty("iteration")).intValue();
        if (!sortedIterationMap.containsKey(iteration)) {
            sortedIterationMap.put(iteration, new ArrayList<>());
        }

        sortedIterationMap.get(iteration).add(response);
    }

    private Long getLowestIterationNumber(List<Long> iterationsDeleted) {
        Collections.sort(iterationsDeleted);
        return iterationsDeleted.get(0);
    }
}
