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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.ArrayList;
import java.util.List;

/*
 * - Checks that the DataPoint (SurveyedLocale) location is correct and updates it using data from
 * the localeGeolocation field of each SurveyInstance
 */
public class CheckDataPointLocation implements Process {

    private boolean fixDataPointLocation = false;
    private int counterNotFound = 0;

    @Override
    public void execute(DatastoreService ds, String[] args) {
        long timeStart = System.currentTimeMillis();
        System.out.println("#Arguments: FIX to correct datapoint location");
        for (String arg : args) {
            if (arg.equalsIgnoreCase("FIX")) {
                fixDataPointLocation = true;
            }
        }

        List<Entity> dataPointsToSave = getDataToFix(ds);
        long timeEnd = System.currentTimeMillis();

        System.out.println("Getting data to fix took: " + (timeEnd - timeStart) + " ms");
        System.out.println("SurveyInstances not Found " + counterNotFound);
        System.out.println(dataPointsToSave.size() + " data points need update");
        if (fixDataPointLocation) {
            if (dataPointsToSave.size() > 0) {
                System.out.println("Will fix data...");
                DataUtils.batchSaveEntities(ds, dataPointsToSave);
                timeEnd = System.currentTimeMillis();
                System.out.println("Getting fixing data took: " + (timeEnd - timeStart) + " ms");
            } else {
                System.out.println("No data to fix...");
            }
        }
    }

    private List<Entity> getDataToFix(DatastoreService ds) {
        List<Entity> modifiedDataPoints = new ArrayList<>();
        Iterable<Entity> entities = getSurveyGroups(ds);
        int surveyCounter = 0;
        for (Entity e : entities) {
            surveyCounter++;
            long surveyId = e.getKey().getId();
            List<Entity> list = getSurveys(ds, surveyId);
            if (list.size() > 1) {
                Long registrationFormId = (Long) e.getProperty("newLocaleSurveyId");
                if (registrationFormId != null) {
                    Entity geoQuestion = getGeoQuestion(ds, registrationFormId);
                    if (geoQuestion != null) {
                        List<Entity> dataPoints = getDataPoints(ds, surveyId);
                        if (dataPoints.size() > 0) {
                            long geoQuestionId = geoQuestion.getKey().getId();
                            for (Entity dataPoint : dataPoints) {
                                Entity dataPointToUpdate = getDataPointToUpdate(ds, dataPoint,
                                        registrationFormId, geoQuestionId);
                                if (dataPointToUpdate != null) {
                                    modifiedDataPoints.add(dataPointToUpdate);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Found " + surveyCounter + " monitored SurveyGroups");
        return modifiedDataPoints;
    }

    private Entity getDataPointToUpdate(DatastoreService ds, Entity dataPoint,
            Long registrationFormId, long geoQuestionId) {
        long dataPointId = dataPoint.getKey().getId();
        long surveyInstanceId = getSurveyInstanceId(ds, registrationFormId,
                (String) dataPoint.getProperty("identifier"), dataPointId);
        if (surveyInstanceId != -1) {
            Entity questionAnswer = getQuestionAnswer(ds, surveyInstanceId, geoQuestionId);
            if (questionAnswer != null) {
                Double dataPointLatitude = (Double) dataPoint.getProperty("latitude");
                Double dataPointLongitude = (Double) dataPoint.getProperty("longitude");

                String answerValue = (String) questionAnswer.getProperty("value");
                if (answerValue != null && !answerValue.isEmpty()) {
                    String[] answerBits = answerValue.split("\\|");
                    if (answerBits.length > 1) {
                        Double answerLatitude = safeParseDouble(answerBits[0]);
                        Double answerLongitude = safeParseDouble(answerBits[1]);

                        boolean dataPointLocationNeedsUpdate =
                                answerLatitude != null && answerLongitude != null
                                        && (!answerLatitude.equals(dataPointLatitude)
                                        || !answerLongitude.equals(dataPointLongitude));
                        if (dataPointLocationNeedsUpdate) {
                            dataPoint.setProperty("latitude", answerLatitude);
                            dataPoint.setProperty("longitude", answerLongitude);

                            System.out.println("Data point " + dataPointId + " needs fixing");
                            return dataPoint;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Double safeParseDouble(String doubleAsString) {
        if (doubleAsString == null || doubleAsString.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(doubleAsString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Entity getQuestionAnswer(DatastoreService ds, long surveyInstanceId, long questionId) {
        Query.Filter f1 = new FilterPredicate("surveyInstanceId", FilterOperator.EQUAL,
                (int) surveyInstanceId);
        Query.Filter f2 = new FilterPredicate("questionID", FilterOperator.EQUAL,
                questionId + "");
        Query.Filter f3 = new FilterPredicate("type", FilterOperator.EQUAL, "GEO");
        Query q = new Query("QuestionAnswerStore");
        q.setFilter(Query.CompositeFilterOperator.and(f1, f2, f3));
        PreparedQuery pq = ds.prepare(q);
        return pq.asSingleEntity();
    }

    private long getSurveyInstanceId(DatastoreService ds, long formId,
            String surveyedLocaleIdentifier, long surveyedLocaleId) {
        Query.Filter f1 = new FilterPredicate("surveyId", FilterOperator.EQUAL, formId);
        Query.Filter f3 = Query.CompositeFilterOperator
                .or(new FilterPredicate("surveyedLocaleId", FilterOperator.EQUAL,
                                (int) surveyedLocaleId),
                        new FilterPredicate("surveyedLocaleIdentifier", FilterOperator.EQUAL,
                                surveyedLocaleIdentifier));
        Query q = new Query("SurveyInstance");
        q.setFilter(Query.CompositeFilterOperator.and(f1, f3));
        q.setKeysOnly();
        PreparedQuery pq = ds.prepare(q);
        Entity surveyInstance = pq.asSingleEntity();
        if (surveyInstance == null) {
            counterNotFound++;
            return -1;
        }
        return surveyInstance.getKey().getId();
    }

    private Entity getGeoQuestion(DatastoreService ds, long surveyId) {
        Query.Filter f1 = new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId);
        Query.Filter f2 = new FilterPredicate("type", FilterOperator.EQUAL, "GEO");
        Query.Filter f3 = new FilterPredicate("localeLocationFlag", FilterOperator.EQUAL,
                Boolean.TRUE);
        Query q = new Query("Question");
        q.setFilter(Query.CompositeFilterOperator.and(f1, f2, f3));
        PreparedQuery pq = ds.prepare(q);
        List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(1));
        if (entities == null || entities.size() == 0) {
            return null;
        }
        return entities.get(0);
    }

    private List<Entity> getDataPoints(DatastoreService ds, long surveyId) {
        Query.Filter f1 = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, surveyId);
        Query q = new Query("SurveyedLocale");
        q.setFilter(f1);
        PreparedQuery pq = ds.prepare(q);
        return pq.asList(FetchOptions.Builder.withDefaults());
    }

    private List<Entity> getSurveys(DatastoreService ds, long surveyId) {
        Query.Filter f1 = new FilterPredicate("surveyGroupId", FilterOperator.EQUAL, surveyId);
        Query.Filter f2 = new FilterPredicate("status", FilterOperator.EQUAL,
                "PUBLISHED");
        Query.CompositeFilter compositeFilter =
                Query.CompositeFilterOperator.and(f1, f2);
        Query q = new Query("Survey").setFilter(compositeFilter).setKeysOnly();

        PreparedQuery pq = ds.prepare(q);
        return pq.asList(FetchOptions.Builder.withLimit(2));
    }

    private Iterable<Entity> getSurveyGroups(DatastoreService ds) {
        Query.Filter f = new FilterPredicate("monitoringGroup", FilterOperator.EQUAL, true);
        Query q = new Query("SurveyGroup").setFilter(f);
        PreparedQuery pq = ds.prepare(q);
        return pq.asIterable();
    }
}
