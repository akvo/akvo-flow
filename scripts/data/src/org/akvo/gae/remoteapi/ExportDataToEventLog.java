package org.akvo.gae.remoteapi;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.akvo.flow.events.EventUtils;
import org.akvo.flow.events.EventUtils.EventTypes;
import org.akvo.flow.events.EventUtils.Kind;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class ExportDataToEventLog implements Process {

    private static final int BATCH_SIZE = 1000;

    private static final String[] kinds = { Kind.SURVEY_GROUP, Kind.FORM,
            Kind.QUESTION_GROUP, Kind.QUESTION, Kind.DATA_POINT,
            Kind.FORM_INSTANCE, Kind.DEVICE_FILE, Kind.ANSWER };

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        if (args.length != 2) {
            System.err
                    .println("Usage: \n"
                            + "This data script depends on code in GAE for generating json event data.\n"
                            + "* Compile the appengine code by running 'ant compile' in the GAE directory. \n"
                            + "* Add GAE/war/WEB-INF/classes and GAE/war/WEB-INF/lib/* to the classpath. \n"
                            + "* Example cli call: \n"
                            + "    java -classpath \"bin:lib/*:../../GAE/war/WEB-INF/classes:../../GAE/war/WEB-INF/lib/*\"\n"
                            + "         org.akvo.gae.remoteapi.RemoteAPI\n"
                            + "         ExportDataToEventLog\n"
                            + "         <app-id>\n"
                            + "         <remote-api-email>\n"
                            + "         <remote-api-password>\n"
                            + "         <app-id>\n"
                            + "         <event-notification-endpoint>");
            return;
        }

        final String orgId = args[0];
        final String servicesEndpoint = args[1];

        for (final String kind : kinds) {
            System.out.println("Exporting " + kind);
            int batch = 0;
            Query kindQuery = new Query(kind);

            final EventTypes eventTypes = EventUtils
                    .getEventAndActionType(kind);

            Iterable<Entity> entities = ds.prepare(kindQuery).asIterable(
                    FetchOptions.Builder.withChunkSize(BATCH_SIZE));

            Iterable<Map<String, Object>> events = Iterables.transform(
                    entities, new Function<Entity, Map<String, Object>>() {
                        @Override
                        public Map<String, Object> apply(Entity entity) {
                            return createEvent(entity, eventTypes, orgId);
                        }
                    });

            Iterable<List<Map<String, Object>>> eventBatches = Iterables
                    .partition(events, BATCH_SIZE);

            for (List<Map<String, Object>> eventBatch : eventBatches) {
                System.out.println("  Batch #" + batch);
                batch++;
                EventUtils.sendEvents(servicesEndpoint + orgId, eventBatch);
            }
        }
    }

    private static Map<String, Object> createEvent(Entity entity,
            EventTypes eventTypes, String orgId) {
        Map<String, Object> source = EventUtils.newSource("import");
        Map<String, Object> context = EventUtils.newContext(new Date(), source);
        context.put("import", true);
        Map<String, Object> entityMap = EventUtils.newEntity(eventTypes.type,
                entity.getKey().getId());
        EventUtils.populateEntityProperties(eventTypes.type, entity, entityMap);
        Map<String, Object> event = EventUtils.newEvent(orgId,
                eventTypes.action + "Created", entityMap, context);
        return event;
    }
}
