package org.akvo.gae.remoteapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

public class CheckParentPath implements Process {

    private static final String SURVEY_GROUP = "SurveyGroup";

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final List<Entity> surveyGroups = ds.prepare(new Query(SURVEY_GROUP))
                .asList(FetchOptions.Builder.withDefaults());

        Map<Long, Long> idToParentId = new HashMap<>();

        for (Entity entity : surveyGroups) {
            Long parentId = (Long) entity.getProperty("parentId");
            parentId = parentId == null ? 0L : parentId;
            idToParentId.put(entity.getKey().getId(), parentId);
        }

        for (Long id : idToParentId.keySet()) {
            List<Long> parentPath = new ArrayList<>();
            checkParentPath(idToParentId, parentPath, id);
        }
    }

    private static final String MISSING_PARENT = "Missing parent for #%s (chain: %s)\n";
    private static final String PATH_LOOP = "Path loop for #%s with parentId #%s and chain %s\n";

    private static void checkParentPath(final Map<Long, Long> idToParentId,
            List<Long> parentPath, final Long id) {
        Long parentId = idToParentId.get(id);
        if (parentId == null) {
            System.out.printf(MISSING_PARENT, id, parentPath);
        } else if (parentPath.contains(parentId)) {
            System.out.printf(PATH_LOOP, id, parentId, parentPath);
        } else if (parentId != 0L) {
            parentPath.add(parentId);
            checkParentPath(idToParentId, parentPath, parentId);
        }
    }
}
