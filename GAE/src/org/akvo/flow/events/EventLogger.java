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

package org.akvo.flow.events;

import static com.gallatinsystems.common.util.MemCacheUtils.initCache;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;

import org.akvo.flow.events.EventUtils.EventSourceType;
import org.akvo.flow.events.EventUtils.EventTypes;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.PostDelete;
import com.google.appengine.api.datastore.PostPut;
import com.google.appengine.api.datastore.PutContext;

public class EventLogger {
    private static Logger logger = Logger.getLogger(EventLogger.class.getName());

    private static final String LAST_UPDATE_DATE_TIME_PROP = "lastUpdateDateTime";
    private static final String CREATED_DATE_TIME_PROP = "createdDateTime";
    private static final String UNIFIED_LOG_NOTIFIED = "unifiedLogNotified";
    private static final String APP_ID_KEY = "appId";
    private static final String EVENT_NOTIFICATION_PROPERTY = "eventNotification";

    private Cache cache;

    public EventLogger() {
        cache = initCache(60); // cache notification for 1 minute
    }

    private void sendNotification(String appId) {
        try {
            String urlPath = PropertyUtil.getProperty(EVENT_NOTIFICATION_PROPERTY);

            if (urlPath == null || urlPath.trim().length() == 0) {
                logger.log(Level.SEVERE, "Event notification URL not present in appengine-web.xml");
                return;
            }

            URL url = new URL(urlPath.trim());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            Map<String, String> messageMap = new HashMap<String, String>();
            messageMap.put(APP_ID_KEY, appId);

            ObjectMapper m = new ObjectMapper();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            m.writeValue(writer, messageMap);
            writer.close();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                logger.log(Level.SEVERE, "Unified log notification failed");
            }
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE,
                    "Unified log notification failed with malformed URL exception", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unified log notification failed with IO exception", e);
        }

    }

    /*
     * Notify the log that a new event is ready to be downloaded. The cache has an expiry of 60
     * seconds, so if another request was fired within that time, we don't do anything.
     */
    private void notifyLog(String appId) {
        if (this.cache != null) {
            if (!cache.containsKey(UNIFIED_LOG_NOTIFIED)) {
                sendNotification(appId);
                cache.put(UNIFIED_LOG_NOTIFIED, null);
                return;
            }
        } else {
            // cache is not accessible, so we will notify anyway
            sendNotification(appId);
        }
    }

    private void storeEvent(Map<String, Object> event, Date timestamp, String appId) {
        try {
            ObjectMapper m = new ObjectMapper();
            StringWriter w = new StringWriter();
            m.writeValue(w, event);
            BaseDAO<EventQueue> eventDao = new BaseDAO<EventQueue>(EventQueue.class);
            EventQueue eventQ = new EventQueue();
            eventQ.setCreatedDateTime(timestamp);
            eventQ.setPayload(w.toString());
            eventDao.save(eventQ);
            notifyLog(appId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "could not store " + event.get("eventType") + " event");
        }
    }

    @PostPut(kinds = {
            "SurveyGroup", "Survey", "QuestionGroup", "Question", "SurveyInstance",
            "QuestionAnswerStore", "SurveyedLocale"
    })
    void logPut(PutContext context) {

        // determine type of event and type of action
        EventTypes types = EventUtils.getEventAndActionType(context.getCurrentElement().getKey()
                .getKind());

        // determine if this entity was created or updated
        String actionType = EventUtils.ACTION_UPDATED;
        if (context.getCurrentElement().getProperty(LAST_UPDATE_DATE_TIME_PROP) == context
                .getCurrentElement().getProperty(CREATED_DATE_TIME_PROP)) {
            actionType = EventUtils.ACTION_CREATED;
        }

        // create event source
        // get the authentication information. This seems to contain the userId, but
        // according to the documentation, should hold the 'password'
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String cred = authentication.getCredentials().toString();

        Map<String, Object> eventSource = EventUtils.newSource(EventSourceType.USER, cred);

        Date timestamp = (Date) context.getCurrentElement().getProperty(LAST_UPDATE_DATE_TIME_PROP);
        // create event context map
        Map<String, Object> eventContext = EventUtils.newContext(timestamp, eventSource);

        // create event entity
        Map<String, Object> eventEntity = EventUtils.newEntity(types.type, context
                .getCurrentElement()
                .getKey().getId());
        EventUtils.populateEntityProperties(types.type, context.getCurrentElement(), eventEntity);

        // create event
        Map<String, Object> event = EventUtils.newEvent(context.getCurrentElement().getAppId(),
                types.action + actionType,
                eventEntity, eventContext);

        // store it
        storeEvent(event, timestamp, context.getCurrentElement().getAppId());
    }

    @PostDelete(kinds = {
            "SurveyGroup", "Survey", "QuestionGroup", "Question", "SurveyInstance",
            "QuestionAnswerStore", "SurveyedLocale"
    })
    void logDelete(DeleteContext context) {
        // determine type of event and type of action
        EventTypes types = EventUtils.getEventAndActionType(context.getCurrentElement().getKind());

        // create event source
        // get the authentication information. This seems to contain the userId, but
        // according to the documentation, should hold the 'password'
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String cred = authentication.getCredentials().toString();

        Map<String, Object> eventSource = EventUtils.newSource(EventSourceType.USER, cred);

        // create event context map
        // we create our own timestamp here, as we don't have one in the context
        Date timestamp = new Date();
        Map<String, Object> eventContext = EventUtils.newContext(timestamp, eventSource);

        // create event entity
        Map<String, Object> eventEntity = EventUtils.newEntity(types.type, context
                .getCurrentElement().getId());

        // create event
        Map<String, Object> event = EventUtils.newEvent(context.getCurrentElement().getAppId(),
                types.action
                        + EventUtils.ACTION_DELETED,
                eventEntity, eventContext);

        // store it
        storeEvent(event, timestamp, context.getCurrentElement().getAppId());
    }
}
