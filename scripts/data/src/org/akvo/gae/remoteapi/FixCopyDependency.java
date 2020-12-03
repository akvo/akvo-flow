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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FixCopyDependency implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        fixDependencyForSurvey(ds);
        // System.out.println(surveysWithMissingDependencies(ds));
    }

    private Set<Long> surveysWithMissingDependencies(DatastoreService ds) {
        Set<Long> brokenSurveys = new HashSet<>();

        Instant t = Instant.parse("2020-11-01T00:00:00Z");

        Query q = new Query("Question");
        Filter f1 = new FilterPredicate("createdDateTime", FilterOperator.GREATER_THAN_OR_EQUAL, Date.from(t));
        q.setFilter(f1);

        PreparedQuery pq = ds.prepare(q);
        int n = 0;
        for (Entity question : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {
            if (Boolean.TRUE.equals(question.getProperty("dependentFlag")) && question.getProperty("dependentQuestionId") == null) {
                System.out.println("Question id " + question.getKey().getId());
                n++;
                brokenSurveys.add((Long) question.getProperty("surveyId"));
            }
        }
        System.out.println("Number of issue " + n);
        return brokenSurveys;
    }

    private void fixDependencyForSurvey(DatastoreService ds) {

        final Query q = new Query("Question");
        Instant t = Instant.parse("2020-11-01T00:00:00Z");
        final Filter theFilter = new FilterPredicate("createdDateTime", FilterOperator.GREATER_THAN_OR_EQUAL, Date.from(t));
        q.setFilter(theFilter);
        q.addSort("createdDateTime", Query.SortDirection.ASCENDING);

        final PreparedQuery pq = ds.prepare(q);

        Map<Long, Entity> questionCache = new HashMap<>();
        Map<Long, Long> dependencyCache = new HashMap<>();
        List<Entity> entitiesToSave = new ArrayList<>();

        for (Entity brokenQuestion : pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            if (brokenQuestion.getProperty("sourceQuestionId") == null) {
                continue;
            }
            if (Boolean.FALSE.equals(brokenQuestion.getProperty("dependentFlag")) ||
                    (Boolean.TRUE.equals(brokenQuestion.getProperty("dependentFlag")) &&
                            brokenQuestion.getProperty("dependentQuestionId") != null)) {
                continue;
            }

            Long sourceQuestionId = (Long) brokenQuestion.getProperty("sourceQuestionId");
            Entity sourceQuestion = null;

            if (!questionCache.containsKey(sourceQuestionId)) {
                try {
                    sourceQuestion = ds.get(KeyFactory.createKey("Question", sourceQuestionId));
                } catch (EntityNotFoundException e) {
                    System.out.println("Source question with id " + sourceQuestionId + " not found, for: " + brokenQuestion.getKey().getId() + " Please check!");
                    continue;
                }
                questionCache.put(sourceQuestionId, sourceQuestion);
            } else {
                sourceQuestion = questionCache.get(sourceQuestionId);
            }

            Long oldDependentId = (Long) sourceQuestion.getProperty("dependentQuestionId");
            if (oldDependentId == null) {
                System.out.println("OldDependantId null for question: " + sourceQuestion.getKey().getId());
                continue;
            }

            if (!dependencyCache.containsKey(oldDependentId)) {

                Entity newDependentQuestion = null;
                try {
                    newDependentQuestion = ds.get(KeyFactory.createKey("Question", oldDependentId));
                } catch (EntityNotFoundException e) {
                    System.out.println("Question with id " + oldDependentId + " not found, for: " + sourceQuestion.getKey().getId() + " Please check!");
                    continue;
                }
                dependencyCache.put(oldDependentId, newDependentQuestion.getKey().getId());
            }

            Long newDependentId = dependencyCache.get(oldDependentId);

            System.out.println(String.format("Setting %s for %s", newDependentId, brokenQuestion.getKey().getId()));

            brokenQuestion.setProperty("dependentQuestionId", newDependentId);
            // entitiesToSave.add(brokenQuestion);
        }

        // ds.put(entitiesToSave);
    }
}
