
package org.akvo.gae.remoteapi;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * A utility class for RemoteAPI data retrieval
 */
public class DataUtils {

    public final static String SURVEY_KIND = "SurveyGroup";

    public final static String FORM_KIND = "Survey";

    public final static String FORM_INSTANCE_KIND = "SurveyInstance";

    public final static String DATA_POINT_KIND = "SurveyInstance";

    public final static String DATA_POINT_STRING_ID = "surveyedLocaleIdentifier";

    public final static String DATA_POINT_NUMERICAL_ID = "surveyedLocaleId";

    private final static int DEFAULT_BATCH_SIZE = 1000;

    /** Save entities in batches of a given size **/
    public static void batchSaveEntities(DatastoreService ds, List<Entity> entities) {
        int startIdx = 0;

        while (startIdx < entities.size()) {
            int endIdx = startIdx + DEFAULT_BATCH_SIZE > entities.size() ? entities.size()
                    : startIdx + DEFAULT_BATCH_SIZE;
            List<Entity> subList = new ArrayList<Entity>(entities.subList(startIdx, endIdx));
            startIdx = endIdx;
            ds.put(subList);
        }
    }

    public static void batchDelete(DatastoreService ds, List<Long> ids, String entityKind) {
        List<Key> entities = new ArrayList<Key>();
        for (Long id : ids) {
            entities.add(KeyFactory.createKey(entityKind, id));
        }
        batchDelete(ds, entities);
    }

    public static void batchDelete(DatastoreService ds, List<Key> entities) {
        int startIdx = 0;

        while (startIdx < entities.size()) {
            int endIdx = startIdx + DEFAULT_BATCH_SIZE > entities.size() ? entities.size()
                    : startIdx + DEFAULT_BATCH_SIZE;
            List<Key> subList = new ArrayList<Key>(entities.subList(startIdx, endIdx));
            startIdx = endIdx;
            ds.delete(subList);
        }
    }
}
