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
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

public class CorrectFolderSurveyPath implements Process {

    private static final String SURVEY_GROUP = "SurveyGroup";
    private static final String SURVEY = "Survey";
    private List<Entity> updatedEntities = new ArrayList<Entity>();

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        System.out.println("Fetching survey groups and surveys.");

        final List<Entity> surveyGroups = ds.prepare(new Query(SURVEY_GROUP)).asList(
                FetchOptions.Builder.withDefaults());
        final List<Entity> surveys = ds.prepare(new Query(SURVEY)).asList(
                FetchOptions.Builder.withDefaults());

        // combine entities to one list
        LinkedList<Entity> completeList = new LinkedList<Entity>();
        completeList.addAll(surveyGroups);
        completeList.addAll(surveys);

        for (Entity rootLevelEntity : completeList) {
            if (rootLevelEntity.getKind().equals(SURVEY_GROUP)
                    && rootLevelEntity.getProperty("parentId") == null) {
                updateFolderSurveyPaths(rootLevelEntity, "", completeList);
            }
        }

        ds.put(updatedEntities);
    }

    private void updateFolderSurveyPaths(Entity entity, String pathPrefix, List<Entity> entities) {
        // set folder/survey path
        String currentPath = (String) entity.getProperty("path");
        String newEntityPath = String.format("%s/%s", pathPrefix, entity.getProperty("name"));
        if (newEntityPath.equals(currentPath)) {
            System.out.println("No changes made for: " + entity.getKey() + " = " + currentPath);
        } else {
            entity.setProperty("path", newEntityPath);
            updatedEntities.add(entity);
            System.out.println("Setting path for: " + entity.getKey() + "; " + currentPath + " => "
                    + newEntityPath);
        }

        // set all child folders/surveys paths
        for (Entity childEntity : entities) {
            Long parentId = getParentId(childEntity);
            if (parentId != null && parentId.equals(entity.getKey().getId())) {
                updateFolderSurveyPaths(childEntity, newEntityPath, entities);
            }
        }
    }

    private Long getParentId(Entity childEntity) {
        if (childEntity.getKind().equals(SURVEY_GROUP)) {
            return (Long) childEntity.getProperty("parentId");
        } else {
            return (Long) childEntity.getProperty("surveyGroupId");
        }
    }
}
