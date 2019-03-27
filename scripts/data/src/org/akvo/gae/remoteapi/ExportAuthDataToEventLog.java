package org.akvo.gae.remoteapi;

import com.google.appengine.api.datastore.*;
import org.akvo.flow.events.EventUtils;
import org.akvo.flow.events.EventUtils.EventTypes;
import org.akvo.flow.events.EventUtils.Kind;

import java.util.Date;
import java.util.Map;

public class ExportAuthDataToEventLog implements Process {

    private static final int BATCH_SIZE = 1000;
    private static final String[] kinds = { Kind.USER, Kind.USER_AUTHORIZATION, Kind.USER_ROLE};

    // The timestamp used for imported data.
    private static final Date IMPORT_DATE = new Date(0);

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {

        final String orgId = args[0];

        for (final String kind : kinds) {
            System.out.println("Exporting " + kind);
            Query kindQuery = new Query(kind);

            final EventTypes eventTypes = EventUtils
                    .getEventAndActionType(kind);

            Iterable<Entity> entities = ds.prepare(kindQuery).asIterable(
                    FetchOptions.Builder.withChunkSize(BATCH_SIZE));

            int amount = 0;
            for (Entity entity : entities) {
                amount ++;
                if (amount % BATCH_SIZE == 0) {
                    System.out.println("progress " + amount);
                }
                Map<String, Object> event = createEvent(entity, eventTypes, orgId);
                Date timestamp = (Date) entity.getProperty(EventUtils.Prop.LAST_UPDATE_DATE_TIME);
                Entity eventLogEntity = EventUtils.createEventLogEntity(event, timestamp);
                ds.put(eventLogEntity);
            }

        }
    }

    private static Map<String, Object> createEvent(Entity entity,
            EventTypes eventTypes, String orgId) {

        Map<String, Object> source = EventUtils.newSource("import");
        Map<String, Object> context = EventUtils
                .newContext(IMPORT_DATE, source);
        context.put("import", true);
        Map<String, Object> entityMap = EventUtils.newEntity(eventTypes.type,
                entity.getKey().getId());
        EventUtils.populateEntityProperties(eventTypes.type, entity, entityMap);
        return EventUtils.newEvent(orgId,
                eventTypes.action + "Created", entityMap, context);
    }
}
