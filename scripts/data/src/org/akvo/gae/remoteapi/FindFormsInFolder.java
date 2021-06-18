/*
 *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindFormsInFolder implements Process {
    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {
        String folderName = args[0];
        long folderId = findFolderId(ds, folderName);
        List<Entity> forms = findFormsInFolder(ds, folderId);
        Map<Long, Entity> surveys = findAssociatedSurveys(ds, forms);
        printFormIds(forms, surveys);
        Entity one = new Entity("Survey");
    }

    private void printFormIds(List<Entity> forms, Map<Long, Entity> surveys) {
        for (Entity form : forms) {
            long formId = form.getKey().getId();
            long surveyId = (long) form.getProperty("surveyGroupId");
            String formName = (String) form.getProperty("name");
            String surveyName;
            if (surveys.get(surveyId) != null) {
                surveyName = (String) surveys.get(surveyId).getProperty("name");
            } else {
                surveyName = "--missing--";
            }

            System.out.println(String.format("%d,%d,%s,%s", formId, surveyId, formName, surveyName));
        }
    }

    private Map<Long, Entity> findAssociatedSurveys(DatastoreService ds, List<Entity> forms) {

        List<Key> surveyKeys = new ArrayList<>();
        for (Entity form : forms) {
            if (form.getProperty("surveyGroupId") != null) {
                Long surveyId = (Long) form.getProperty("surveyGroupId");
                surveyKeys.add(KeyFactory.createKey("SurveyGroup", surveyId));
            }
        }

        Map<Long, Entity> surveysMap = new HashMap<>();
        for (Map.Entry<Key, Entity> entry : ds.get(surveyKeys).entrySet()) {
            surveysMap.put(entry.getKey().getId(), entry.getValue());
        }
        return surveysMap;
    }

    private long findFolderId(DatastoreService ds, String folderName) {
        Query.Filter folderNameFilter = new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, folderName);
        Query.Filter folderTypeFilter = new Query.FilterPredicate("projectType", Query.FilterOperator.EQUAL, "PROJECT_FOLDER");
        Query.Filter combinedFilter = Query.CompositeFilterOperator.and(folderNameFilter, folderTypeFilter);

        Query findFolder = new Query("SurveyGroup")
                                .setFilter(combinedFilter);
        final PreparedQuery pq = ds.prepare(findFolder);
        List<Entity> folders = pq.asList(FetchOptions.Builder.withDefaults());

        if (folders.isEmpty()) {
            System.out.println("Folder not found");
            System.exit(0);
        }

        return folders.get(0).getKey().getId();
    }

    private List<Entity> findFormsInFolder(DatastoreService ds, long folderId) {
        Query.Filter formsFilter = new Query.FilterPredicate("ancestorIds", Query.FilterOperator.EQUAL, folderId);
        Query findForms = new Query("Survey").setFilter(formsFilter);
        final PreparedQuery pq = ds.prepare(findForms);

        List<Entity> forms = new ArrayList<>();
        for (Entity form : pq.asList(FetchOptions.Builder.withDefaults())) {
            long formId = form.getKey().getId();
            long surveyId = (long) form.getProperty("surveyGroupId");
            String formName = (String) form.getProperty("name");
            String path = (String) form.getProperty("path");
            forms.add(form);
        }
        return forms;
    }
}
