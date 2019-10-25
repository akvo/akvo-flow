/*
 *  Copyright (C) 2015,2019 Stichting Akvo (Akvo Foundation)
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;

import org.akvo.flow.events.EventUtils.*;
import org.akvo.flow.util.FlowJsonObjectWriter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.common.util.PropertyUtil;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PostDelete;
import com.google.appengine.api.datastore.PostPut;
import com.google.appengine.api.datastore.PutContext;
import com.google.appengine.api.utils.SystemProperty;

import static com.gallatinsystems.common.util.MemCacheUtils.initCache;
import static org.akvo.flow.events.EventUtils.*;

public class EventLogger {
    private static Logger logger = Logger.getLogger(EventLogger.class.getName());

    private static final long MIN_TIME_DIFF = 60000; // 60 seconds

    private void sendNotification() {
        sendNotificationToUnilog(PropertyUtil.getProperty(Prop.EVENT_NOTIFICATION), SystemProperty.applicationId.get());
    }

    public static void sendNotificationToUnilog(String urlPath, String appId) {
        try {
            if (urlPath == null || urlPath.trim().length() == 0) {
                return;
            }

            URL url = new URL(urlPath.trim());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            Map<String, String> messageMap = new HashMap<String, String>();
            messageMap.put(Key.APP_ID, appId);
            messageMap.put(Key.URL, appId + ".appspot.com");

            FlowJsonObjectWriter writer = new FlowJsonObjectWriter();
            writer.writeValue(connection.getOutputStream(), messageMap);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                logger.log(Level.SEVERE, "Unified log notification failed with status code: "
                        + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE,
                    "Unified log notification failed with malformed URL exception", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unified log notification failed with IO exception", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unified log notification failed with error", e);
        }
    }

    /*
     * Notify the log that a new event is ready to be downloaded.
     */
    private void notifyLog() {
        Cache cache = initCache(60 * 60); // 1 hour
        if (cache == null) {
            // cache is not accessible, so we will notify anyway
            logger.log(Level.WARNING,
                    "cache not accessible, but still sending notification to unified log");
            sendNotification();
            return;
        }

        if (cache.containsKey(Action.UNIFIED_LOG_NOTIFIED)) {
            // check if the time the last notification was send is less than one minute ago
            Date cacheDate = (Date) cache.get(Action.UNIFIED_LOG_NOTIFIED);
            Date nowDate = new Date();
            Long deltaMils = nowDate.getTime() - cacheDate.getTime();
            if (deltaMils < MIN_TIME_DIFF) {
                // it is too soon, so don't send the notification
                return;
            }
        }
        // if we are here, either the key is not in the cache, or it is too old
        // in both cases, we send the notification and add a fresh value to the cache
        sendNotification();
        cache.put(Action.UNIFIED_LOG_NOTIFIED, new Date());
    }

    private void storeEvent(Map<String, Object> event, Date timestamp) {
        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            Entity entity = EventUtils.createEventLogEntity(event, timestamp);

            datastore.put(entity);
            notifyLog();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "could not store " + event.get("eventType")
                    + " event. Error: " + e.toString(), e);
        }
    }

    @PostPut(kinds = {
            "SurveyGroup", "Survey", "QuestionGroup", "Question", "SurveyInstance",
            "QuestionAnswerStore", "SurveyedLocale", "DeviceFiles", "UserRole", "User", "UserAuthorization"
    })
    void logPut(PutContext context) {

        try {
            if (!"true".equals(PropertyUtil.getProperty(Prop.ENABLE_CHANGE_EVENTS))) {
                return;
            }

            Entity current = context.getCurrentElement();

            // determine type of event and type of action
            EventTypes types = getEventAndActionType(current.getKey().getKind());

            // determine if this entity was created or updated
            Date lastUpdateDatetime = (Date) current.getProperty(Prop.LAST_UPDATE_DATE_TIME);
            Date createdDateTime = (Date) current.getProperty(Prop.CREATED_DATE_TIME);
            String actionType = createdDateTime.equals(lastUpdateDatetime) ? Action.CREATED
                    : Action.UPDATED;

            // create event source
            // get the authentication information. This seems to contain the userId, but
            // according to the documentation, should hold the 'password'
            final Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();

            Map<String, Object> eventSource = newSource(authentication.getPrincipal());

            Date timestamp = (Date) context.getCurrentElement().getProperty(
                    Prop.LAST_UPDATE_DATE_TIME);
            // create event context map
            Map<String, Object> eventContext = newContext(timestamp, eventSource);

            // create event entity
            Map<String, Object> eventEntity = newEntity(types.type, context
                    .getCurrentElement().getKey().getId());

            populateEntityProperties(types.type, context.getCurrentElement(), eventEntity);

            // create event
            Map<String, Object> event = newEvent(SystemProperty.applicationId.get(),
                    types.action + actionType, eventEntity, eventContext);

            // store it
            storeEvent(event, timestamp);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not handle datastore put event: " + e.getMessage(), e);
        }
    }

    @PostDelete(kinds = {
            "SurveyGroup", "Survey", "QuestionGroup", "Question", "SurveyInstance",
            "QuestionAnswerStore", "SurveyedLocale", "DeviceFiles", "UserRole", "User", "UserAuthorization"
    })
    void logDelete(DeleteContext context) {
        try {
            if (!"true".equals(PropertyUtil.getProperty(Prop.ENABLE_CHANGE_EVENTS))) {
                return;
            }

            // determine type of event and type of action
            EventTypes types = getEventAndActionType(context.getCurrentElement().getKind());

            // create event source
            // get the authentication information. This seems to contain the userId, but
            // according to the documentation, should hold the 'password'
            final Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();
            Object principal = authentication.getPrincipal();

            Map<String, Object> eventSource = newSource(principal);

            // create event context map
            // we create our own timestamp here, as we don't have one in the context
            Date timestamp = new Date();
            Map<String, Object> eventContext = newContext(timestamp, eventSource);

            // create event entity
            Map<String, Object> eventEntity = newEntity(types.type, context
                    .getCurrentElement().getId());

            // create event
            Map<String, Object> event = newEvent(SystemProperty.applicationId.get(),
                    types.action + Action.DELETED, eventEntity, eventContext);

            // store it
            storeEvent(event, timestamp);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not handle datastore delete event: " + e.getMessage(),
                    e);
        }
    }
}
