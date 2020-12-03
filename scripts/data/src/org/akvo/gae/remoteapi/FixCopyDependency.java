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
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class FixCopyDependency implements Process {

    private static final Map<Long, Entity> questionCache = new HashMap<>();
    private static final Map<String, Entity> dependencyCache = new HashMap<>();


    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        //brokenSurveysWithData(ds);
        fixDependencyForSurveys(ds);
    }

    private void brokenSurveysWithData(DatastoreService ds) {
        for (Long surveyId : surveysWithMissingDependencies(ds)) {
            if (getDataForSurvey(ds, surveyId).size() > 0) {
                System.out.println("Survey id " + surveyId + " has data");
            }
        }
    }

    private List<Long> surveysWithMissingDependencies(DatastoreService ds) {
        final Set<Long> brokenSurveys = new HashSet<>();
        final Date t = Date.from(Instant.parse("2020-10-01T00:00:00Z"));

        final Query q = new Query("Question")
                .setFilter(new FilterPredicate("createdDateTime", FilterOperator.GREATER_THAN_OR_EQUAL, t))
                .addSort("createdDateTime", Query.SortDirection.ASCENDING);

        int n = 0;
        for (Entity question : ds.prepare(q).asIterable(FetchOptions.Builder.withChunkSize(500))) {
            if (question.getProperty("sourceQuestionId") != null &&
                    Boolean.TRUE.equals(question.getProperty("dependentFlag")) &&
                    question.getProperty("dependentQuestionId") == null) {
                n++;
                brokenSurveys.add((Long) question.getProperty("surveyId"));
            }
        }
        System.out.println("Broken questions: " + n);
        System.out.println("Broken surveys: " + brokenSurveys.size());
        return new ArrayList<>(brokenSurveys);
    }

    private List<Entity> getDataForSurvey(DatastoreService ds, Long surveyId) {
        final Query q = new Query("SurveyInstance")
                .setFilter(new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId))
                .setKeysOnly();
        return ds.prepare(q).asList(FetchOptions.Builder.withChunkSize(500));
    }

    private void fixDependencyForSurveys(DatastoreService ds) {

        List<Entity> copiedQuestions = getBrokenQuestions(ds);

        for (Entity question : copiedQuestions) {

            Long sourceQuestionId = (Long) question.getProperty("sourceQuestionId");
            Entity sourceQuestion = getSourceQuestion(ds, sourceQuestionId);

            if (sourceQuestion == null) {
                System.out.println("Broken sourceQuestionId for question: " + question.getKey().getId());
                continue;
            }

            Long sourceDependentQuestionId = (Long) sourceQuestion.getProperty("dependentQuestionId");

            if (sourceDependentQuestionId == null) {
                System.out.println("Source dependentQuestionId is null for question: "
                        + sourceQuestion.getKey().getId()
                        + " broken question: " + question.getKey().getId());
                continue;
            }

            Long surveyId = (Long) question.getProperty("surveyId");
            Entity newDependent = getNewDependentQuestion(ds, surveyId, sourceDependentQuestionId);

            if (newDependent == null) {
                System.out.println("New dependent question for source id " + sourceDependentQuestionId + " not found");
                continue;
            }

            setNewDependency(question, newDependent);

            // entitiesToSave.add(brokenQuestion);
        }

        // ds.put(entitiesToSave);
    }

    private List<Entity> getBrokenQuestions(DatastoreService ds) {
        Date t = Date.from(Instant.parse("2020-10-01T00:00:00Z"));

        Query qg = new Query("QuestionGroup")
                .setFilter(new FilterPredicate("createdDateTime", FilterOperator.GREATER_THAN_OR_EQUAL, t))
                .addSort("createdDateTime", Query.SortDirection.ASCENDING);

        Map<Long, Integer> groupOrder = new HashMap<>();

        for (Entity group : ds.prepare(qg).asList(FetchOptions.Builder.withChunkSize(500))) {
            groupOrder.put(group.getKey().getId(), ((Long) group.getProperty("order")).intValue());
        }

        Query q = new Query("Question")
                .setFilter(new FilterPredicate("createdDateTime", FilterOperator.GREATER_THAN_OR_EQUAL, t))
                .addSort("createdDateTime", Query.SortDirection.ASCENDING);

        List<Entity> candidateQuestions = ds.prepare(q).asList(FetchOptions.Builder.withChunkSize(500));
        List<Entity> copiedQuestions = candidateQuestions
                .stream()
                .filter(entity -> entity.getProperty("sourceQuestionId") != null
                        && Boolean.TRUE.equals(entity.getProperty("dependentFlag")) &&
                        entity.getProperty("dependentQuestionId") == null)
                .collect(Collectors.toList());

        copiedQuestions.sort((entity1, entity2) -> {
            Integer order1 = ((Long) entity1.getProperty("order")).intValue();
            Integer order2 = ((Long) entity2.getProperty("order")).intValue();

            Long group1 = (Long) entity1.getProperty("questionGroupId");
            Long group2 = (Long) entity2.getProperty("questionGroupId");

            if (group1.equals(group2)) {
                return order1.compareTo(order2);
            }

            Integer orderWithGroup1 = groupOrder.get(group1) * order1;
            Integer orderWithGroup2 = groupOrder.get(group2) * order2;

            return orderWithGroup1.compareTo(orderWithGroup2);
        });

        return copiedQuestions;
    }

    private Entity getSourceQuestion(DatastoreService ds, Long sourceQuestionId) {
        if (questionCache.containsKey(sourceQuestionId)) {
            return questionCache.get(sourceQuestionId);
        }
        Entity q = null;
        try {
            q = ds.get(KeyFactory.createKey("Question", sourceQuestionId));
        } catch (EntityNotFoundException e) {
            System.out.println("Source question with id " + sourceQuestionId + " not found");
        }
        questionCache.put(sourceQuestionId, q);
        return q;
    }

    private Entity getNewDependentQuestion(DatastoreService ds, Long surveyId, Long sourceDependentQuestionId) {
        String key = surveyId + "-" + sourceDependentQuestionId;

        if (dependencyCache.containsKey(key)) {
            return dependencyCache.get(key);
        }

        Entity q = null;

        Query query = new Query("Question");
        Filter sourceFilter = new FilterPredicate("sourceQuestionId", FilterOperator.EQUAL, sourceDependentQuestionId);
        Filter surveyFilter = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
        Filter f = new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(surveyFilter, sourceFilter));
        query.setFilter(f);

        q = ds.prepare(query).asSingleEntity();

        dependencyCache.put(key, q);
        return q;
    }

    private void setNewDependency(Entity question, Entity newDependent) {
        System.out.println(String.format("Setting %s for %s", newDependent.getKey().getId(), question.getKey().getId()));
        question.setProperty("dependentQuestionId", newDependent.getKey().getId());
        questionCache.put(question.getKey().getId(), question);
    }
}
