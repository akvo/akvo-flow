package org.akvo.gae.remoteapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

public class PathToIdMigration implements Process {

    private static final String SURVEY_GROUP = "SurveyGroup";
    private static final String SURVEY = "Survey";

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final List<Entity> surveyGroups = ds.prepare(new Query(SURVEY_GROUP))
                .asList(FetchOptions.Builder.withDefaults());
        final List<Entity> surveys = ds.prepare(new Query(SURVEY)).asList(
                FetchOptions.Builder.withDefaults());

        Map<Long, Long> idToParentId = new HashMap<>();

        for (Entity entity : surveyGroups) {
            // Ensure root folder parentId == 0
            if (entity.getProperty("parentId") == null) {
                entity.setProperty("parentId", 0L);
            }
            idToParentId.put(entity.getKey().getId(),
                    (Long) entity.getProperty("parentId"));
        }

        for (Entity entity : surveys) {
            Long entityId = entity.getKey().getId();
            Long surveyGroupId = (Long) entity.getProperty("surveyGroupId");
            if (surveyGroupId != null) {
                idToParentId.put(entityId, surveyGroupId);
            }
        }

        List<Entity> surveysAndSurveyGroups = new ArrayList<>();
        surveysAndSurveyGroups.addAll(surveyGroups);
        surveysAndSurveyGroups.addAll(surveys);

        for (Entity entity : surveysAndSurveyGroups) {
            List<Long> aIds = new ArrayList<>();
            Long entityId = entity.getKey().getId();
            ancestorIds(idToParentId, aIds, entityId);
            if (aIds.isEmpty() || aIds.get(0) != 0) {
                System.out.println(String.format("Could not generate ancestorIds for Survey or Survey group #%s", entityId));
            } else {
                entity.setProperty("ancestorIds", aIds);
            }
        }

        ds.put(surveysAndSurveyGroups);
    }

    private static void ancestorIds(final Map<Long, Long> idToParentId,
            List<Long> aIds, final Long id) {

        Long parentId = idToParentId.get(id);
        if (parentId == null) {
            return;
        }

        aIds.add(parentId);
        if (parentId == 0) {
            Collections.reverse(aIds);
            return;
        } else {
            ancestorIds(idToParentId, aIds, parentId);
        }

    }
}
