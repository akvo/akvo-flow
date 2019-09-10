package org.akvo.gae.remoteapi;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.akvo.flow.events.EventUtils;

import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import static org.akvo.flow.events.EventUtils.*;

public class DataStoreWithUnilog implements DatastoreService {
    private static final Date MANUAL_FIX_START_TIME = new Date();
    private DatastoreService delegate;
    private String orgId;

    DataStoreWithUnilog(DatastoreService delegate, String orgId) {
        this.delegate = delegate;
        this.orgId = orgId;
    }

    @Override
    public Key put(Entity entity) {
        Key put = delegate.put(entity);
        storeEntityUpdate(Stream.of(entity));
        return put;
    }

    @Override
    public Key put(Transaction transaction, Entity entity) {
        Key put = delegate.put(transaction, entity);
        storeEntityUpdate(Stream.of(entity));
        return put;
    }

    @Override
    public List<Key> put(Iterable<Entity> iterable) {
        List<Key> put = delegate.put(iterable);
        Stream<Entity> entities = asStream(iterable);
        storeEntityUpdate(entities);
        return put;
    }

    @Override
    public List<Key> put(Transaction transaction, Iterable<Entity> iterable) {
        List<Key> put = delegate.put(transaction, iterable);
        storeEntityUpdate(asStream(iterable));
        return put;
    }

    @Override
    public void delete(Key... keys) {
        delegate.delete(keys);
        storeDeleteEvents(Arrays.stream(keys));
    }

    @Override
    public void delete(Transaction transaction, Key... keys) {
        delegate.delete(transaction, keys);
        storeDeleteEvents(Arrays.stream(keys));
    }

    @Override
    public void delete(Iterable<Key> iterable) {
        delegate.delete(iterable);
        storeDeleteEvents(asStream(iterable));
    }

    @Override
    public void delete(Transaction transaction, Iterable<Key> iterable) {
        delegate.delete(transaction, iterable);
        storeDeleteEvents(asStream(iterable));
    }

    private <T> Stream<T> asStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private void storeEntityUpdate(Stream<Entity> entities) {
        storeInEventQueue(entities.map(this::event));
    }

    private void storeInEventQueue(Stream<Entity> entityStream) {
        List<Entity> events = entityStream
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!events.isEmpty()) {
            delegate.put(events);
        }
    }

    private Map<String, Object> manualFixEventSource() {
        return EventUtils.newSource("manualDataFix");
    }

    private Entity event(Entity entity) {
        String kind = entity.getKey().getKind();
        final EventTypes eventTypes = EventUtils
                .getEventAndActionType(kind);

        if (eventTypes != null) {
            Map<String, Object> event = updateOrCreateEvent(entity, eventTypes);
            try {
                return EventUtils.createEventLogEntity(event, new Date());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    private Map<String, Object> updateOrCreateEvent(Entity entity,
                                                    EventUtils.EventTypes eventTypes) {
        String actionType = eventActionType(entity);
        Map<String, Object> context = manualFixContext();
        Map<String, Object> entityMap = EventUtils.newEntity(eventTypes.type,
                entity.getKey().getId());
        EventUtils.populateEntityProperties(eventTypes.type, entity, entityMap);
        return EventUtils.newEvent(orgId,
                eventTypes.action + actionType, entityMap, context);
    }

    private Map<String, Object> manualFixContext() {
        return EventUtils
                .newContext(MANUAL_FIX_START_TIME, manualFixEventSource());
    }

    private String eventActionType(Entity entity) {
        Date lastUpdateDatetime = (Date) entity.getProperty(EventUtils.Prop.LAST_UPDATE_DATE_TIME);
        Date createdDateTime = (Date) entity.getProperty(EventUtils.Prop.CREATED_DATE_TIME);
        return createdDateTime.equals(lastUpdateDatetime) ? EventUtils.Action.CREATED
                : EventUtils.Action.UPDATED;
    }

    private void storeDeleteEvents(Stream<Key> stream) {
        storeInEventQueue(stream.map(this::deleteEvent));
    }

    private Entity deleteEvent(Key key) {
        EventTypes types = getEventAndActionType(key.getKind());
        if (types == null) {
            return null;
        }

        Map<String, Object> eventContext = newContext(MANUAL_FIX_START_TIME, manualFixEventSource());

        Map<String, Object> eventEntity = newEntity(types.type, key.getId());

        Map<String, Object> event = newEvent(orgId,
                types.action + Action.DELETED, eventEntity, eventContext);

        try {
            return EventUtils.createEventLogEntity(event, new Date());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //** No other method modified **//

    @Override
    public Entity get(Key key) throws EntityNotFoundException {
        return delegate.get(key);
    }

    @Override
    public Entity get(Transaction transaction, Key key) throws EntityNotFoundException {
        return delegate.get(transaction, key);
    }

    @Override
    public Map<Key, Entity> get(Iterable<Key> iterable) {
        return delegate.get(iterable);
    }

    @Override
    public Map<Key, Entity> get(Transaction transaction, Iterable<Key> iterable) {
        return delegate.get(transaction, iterable);
    }


    @Override
    public Transaction beginTransaction() {
        return delegate.beginTransaction();
    }

    @Override
    public Transaction beginTransaction(TransactionOptions transactionOptions) {
        return delegate.beginTransaction(transactionOptions);
    }

    @Override
    public KeyRange allocateIds(String s, long l) {
        return delegate.allocateIds(s, l);
    }

    @Override
    public KeyRange allocateIds(Key key, String s, long l) {
        return delegate.allocateIds(key, s, l);
    }

    @Override
    public KeyRangeState allocateIdRange(KeyRange keyRange) {
        return delegate.allocateIdRange(keyRange);
    }

    @Override
    public DatastoreAttributes getDatastoreAttributes() {
        return delegate.getDatastoreAttributes();
    }

    @Override
    public Map<Index, Index.IndexState> getIndexes() {
        return delegate.getIndexes();
    }

    @Override
    public PreparedQuery prepare(Query query) {
        return delegate.prepare(query);
    }

    @Override
    public PreparedQuery prepare(Transaction transaction, Query query) {
        return delegate.prepare(transaction, query);
    }

    @Override
    public Transaction getCurrentTransaction() {
        return delegate.getCurrentTransaction();
    }

    @Override
    public Transaction getCurrentTransaction(Transaction transaction) {
        return delegate.getCurrentTransaction(transaction);
    }

    @Override
    public Collection<Transaction> getActiveTransactions() {
        return delegate.getActiveTransactions();
    }
}
