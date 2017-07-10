/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

import static org.akvo.gae.remoteapi.DataUtils.batchSaveEntities;


/**
 * Check for survey data that is publicly visible and correct its privacyLevel
 */
public class HidePrivateSurveyData implements Process {

    private static final int MAX_UNDERLYING_QUERIES = 30;

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        if (args.length == 0) {
            System.err.println("Usage: " + RemoteAPI.class.getName()
                    + "HidePrivateSurveyData <appid> <username> <password> <appid> [--dry-run]");
            System.exit(1);
        }
        String appId = args[0];

        // retrieve all private survey group ids
        Filter projectTypeFilter = new Query.FilterPredicate("projectType", FilterOperator.EQUAL,
                "PROJECT");
        Filter privateLevelFilter = new Query.FilterPredicate("privacyLevel",
                FilterOperator.EQUAL, "PRIVATE");
        Filter privateSurveyGroupFilter = CompositeFilterOperator.and(projectTypeFilter,
                privateLevelFilter);

        Query privateSurveyGroupQuery = new Query("SurveyGroup")
                .setFilter(privateSurveyGroupFilter).setKeysOnly();

        Set<Long> privateSurveyGroupIds = new HashSet<Long>();
        for (Entity surveyGroup : ds.prepare(privateSurveyGroupQuery).asIterable()) {
            privateSurveyGroupIds.add(surveyGroup.getKey().getId());
        }
        System.out.printf("Found %d private leaf SurveyGroups\n", privateSurveyGroupIds.size());

        // retrieve survey ids for private groups
        Query surveyQuery = new Query("Survey").setFilter(new Query.FilterPredicate(
                "surveyGroupId", FilterOperator.IN, privateSurveyGroupIds));
        Set<Long> privateSurveyIds = new HashSet<Long>();
        for (Entity survey : ds.prepare(surveyQuery).asIterable()) {
            String pointType = (String) survey.getProperty("pointType");

            // forms created after introduction of folders don't have a pointType so adopt privacy
            // level from above selected 'PRIVATE' survey groups (folder)
            if (pointType == null || pointType.equals("Household")) {
                privateSurveyIds.add(survey.getKey().getId());
            }
        }
        System.out.printf("Found %d private Surveys\n", privateSurveyIds.size());
        
        // retrieve survey instances for private surveys
        Query surveyResponses = new Query("SurveyInstance").setFilter(
                new Query.FilterPredicate("surveyId", FilterOperator.IN, privateSurveyIds))
                .setKeysOnly();
        List<Long> surveyInstanceIds = new ArrayList<Long>();
        for (Entity surveyInstance : ds.prepare(surveyResponses).asIterable(
                FetchOptions.Builder.withChunkSize(1000))) {
            surveyInstanceIds.add(surveyInstance.getKey().getId());
        }
        System.out.printf("Found %d instances\n", surveyInstanceIds.size());
        
        
        // identify publicly visible surveyed locales for private surveys
        List<Entity> publiclyVisibleLocalesList = new ArrayList<Entity>();
        int startIdx = 0;

        // process locales in batches of size = MAX_UNDERLYING_QUERY
        while (startIdx < surveyInstanceIds.size()) {
            final int endIdx = startIdx + MAX_UNDERLYING_QUERIES > surveyInstanceIds.size() ? surveyInstanceIds
                    .size()
                    : startIdx + MAX_UNDERLYING_QUERIES;

            final List<Long> instancesSubList = new ArrayList<Long>(surveyInstanceIds.subList(
                    startIdx, endIdx));

            startIdx = endIdx;

            Filter surveyInstanceIdFilter = new Query.FilterPredicate("lastSurveyalInstanceId",
                    Query.FilterOperator.IN, instancesSubList);

            Query localesQuery = new Query("SurveyedLocale").setFilter(surveyInstanceIdFilter);

            PreparedQuery pqLocales = ds.prepare(localesQuery);
            for (Entity entity : pqLocales.asIterable()) {
                String localeType = (String) entity.getProperty("localeType");
                if (!localeType.trim().equals("Household") && !localeType.trim().equals("PRIVATE")) {
                    publiclyVisibleLocalesList.add(entity);
                    System.out.println(appId + "," + entity.getKey().getId() + "," + localeType);
                }
            }
        }

        // dry run exit
        boolean dryrun = args.length == 2 && args[1].equals("--dry-run");
        if (dryrun) {
            System.out.println("Found " + publiclyVisibleLocalesList.size()
                    + "publicly visible private data points");
            System.out.println("Exiting without updating any data points");
            System.exit(0);
        }

        // update locales
        if (publiclyVisibleLocalesList.size() > 0) {
            System.out.println("Setting " + publiclyVisibleLocalesList.size()
                    + " surveyedLocales to PRIVATE");
            for (Entity locale : publiclyVisibleLocalesList) {
                locale.setProperty("localeType", "PRIVATE");
            }
            System.out.printf("Updating %d instances\n", publiclyVisibleLocalesList.size());
            batchSaveEntities(ds,publiclyVisibleLocalesList);
        }

    }
}
