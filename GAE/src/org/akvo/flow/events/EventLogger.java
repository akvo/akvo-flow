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

import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.akvo.flow.events.EventUtils.EventSourceType;
import org.akvo.flow.events.EventUtils.EventTypes;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.PostDelete;
import com.google.appengine.api.datastore.PostPut;
import com.google.appengine.api.datastore.PutContext;

public class EventLogger {
    private static Logger logger = Logger.getLogger(EventLogger.class.getName());
    
    private static final String LAST_UPDATE_DATE_TIME_PROP = "lastUpdateDateTime";
    private static final String CREATED_DATE_TIME_PROP = "createdDateTime";

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private void storeEvent(Map<String, Object> event, Date timestamp) {
        try {
            ObjectMapper m = new ObjectMapper();
            StringWriter w = new StringWriter();
            m.writeValue(w, event);
            BaseDAO<EventQueue> eventDao = new BaseDAO(EventQueue.class);
            EventQueue eventQ = new EventQueue(timestamp, w.toString());
            eventDao.save(eventQ);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, "could not store " + event.get("eventType") + " event");
            e.printStackTrace();
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
        storeEvent(event, timestamp);
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
        storeEvent(event, timestamp);
    }
}
