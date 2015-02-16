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

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;

public class ProjectMigration implements Process {

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        for (Entity surveyGroup : ds.prepare(new Query("SurveyGroup"))
                .asIterable()) {

            // If the surveyGroup already has projectType set, don't touch it.
            if (surveyGroup.getProperty("projectType") != null) {
                continue;
            }

            long surveyGroupId = surveyGroup.getKey().getId();
            String surveyGroupPath = "/" + surveyGroup.getProperty("name");

            Filter surveyGroupFilter = new Query.FilterPredicate(
                    "surveyGroupId", Query.FilterOperator.EQUAL, surveyGroupId);
            Query surveyQuery = new Query("Survey")
                    .setFilter(surveyGroupFilter);
            List<Entity> surveys = ds.prepare(surveyQuery).asList(
                    FetchOptions.Builder.withDefaults());

            boolean isMonitoring = Boolean.TRUE.equals(surveyGroup
                    .getProperty("monitoringGroup"));

            if (isMonitoring || surveys.size() <= 1) {
                surveyGroup.setProperty("projectType", "PROJECT");
                String privacyLevel = "PUBLIC";
                String defaultLanguageCode = "en";
                for (Entity survey : surveys) {
                    if ("Household".equals(survey.getProperty("pointType"))) {
                        privacyLevel = "PRIVATE";
                    }
                    survey.setProperty("path",
                            surveyGroupPath + "/" + survey.getProperty("name"));

                }

                if (surveys.size() >= 1) {
                    defaultLanguageCode = (String) surveys.get(0).getProperty(
                            "defaultLanguageCode");
                }

                surveyGroup.setProperty("parentId", null);
                surveyGroup.setProperty("privacyLevel", privacyLevel);
                surveyGroup.setProperty("defaultLanguageCode",
                        defaultLanguageCode);
                surveyGroup.setProperty("path", surveyGroupPath);

                ds.put(surveys);
                ds.put(surveyGroup);

            } else {
                surveyGroup.setProperty("projectType", "PROJECT_FOLDER");
                for (Entity survey : surveys) {
                    Entity newSurveyGroup = new Entity("SurveyGroup");
                    Object surveyName = survey.getProperty("name");
                    String newSurveyGroupPath = surveyGroupPath + "/"
                            + surveyName;

                    newSurveyGroup.setProperty("name", surveyName);
                    newSurveyGroup.setProperty("code", surveyName);
                    newSurveyGroup.setProperty("parentId", surveyGroupId);
                    newSurveyGroup.setProperty("monitoringGroup", false);
                    newSurveyGroup.setProperty("projectType", "PROJECT");
                    newSurveyGroup.setProperty("defaultLanguageCode",
                            survey.getProperty("defaultLanguageCode"));
                    newSurveyGroup.setProperty("path", newSurveyGroupPath);
                    newSurveyGroup
                            .setProperty(
                                    "privacyLevel",
                                    "Household".equals(survey
                                            .getProperty("pointType")) ? "PRIVATE"
                                            : "PUBLIC");

                    long newSurveyGroupId = ds.put(newSurveyGroup).getId();

                    survey.setProperty("surveyGroupId", newSurveyGroupId);
                    survey.setProperty("path", newSurveyGroupPath + "/"
                            + surveyName);
                }
                ds.put(surveys);
            }
        }
    }
}