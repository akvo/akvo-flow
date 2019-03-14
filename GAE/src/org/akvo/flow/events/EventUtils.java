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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.akvo.flow.util.FlowJsonObjectWriter;
import org.waterforpeople.mapping.app.web.rest.security.user.GaeUser;

import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.PrivacyLevel;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class EventUtils {

    private static Logger log = Logger.getLogger(EventUtils.class.getName());

    public enum EventSourceType {
        USER, DEVICE, SENSOR, WEBFORM, API, UNKNOWN, SYSTEM
    };

    public enum EntityType {
        SURVEY_GROUP, FORM, QUESTION_GROUP, QUESTION, DATA_POINT, FORM_INSTANCE, ANSWER, DEVICE_FILE
    };

    // names of kinds in Google App Engine
    static public class Kind {
        public static final String SURVEY_GROUP = "SurveyGroup";
        public static final String FORM = "Survey";
        public static final String QUESTION_GROUP = "QuestionGroup";
        public static final String QUESTION = "Question";
        public static final String DATA_POINT = "SurveyedLocale";
        public static final String FORM_INSTANCE = "SurveyInstance";
        public static final String ANSWER = "QuestionAnswerStore";
        public static final String DEVICE_FILE = "DeviceFiles";
    }

    // How we name the actions
    static class Action {
        public static final String SURVEY_GROUP = "surveyGroup";
        public static final String FORM = "form";
        public static final String QUESTION_GROUP = "questionGroup";
        public static final String QUESTION = "question";
        public static final String DATA_POINT = "dataPoint";
        public static final String FORM_INSTANCE = "formInstance";
        public static final String ANSWER = "answer";
        public static final String DEVICE_FILE = "deviceFile";

        public static final String DELETED = "Deleted";
        public static final String CREATED = "Created";
        public static final String UPDATED = "Updated";

        public static final String UNIFIED_LOG_NOTIFIED = "unifiedLogNotified";

    }

    static class Key {
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String DISPLAY_TEXT = "displayText";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String TIMESTAMP = "timestamp";
        public static final String SOURCE = "source";
        public static final String TYPE = "type";
        public static final String ORDER = "order";
        public static final String LAT = "lat";
        public static final String LON = "lon";
        public static final String ORG_ID = "orgId";
        public static final String SURVEY_ID = "surveyId";
        public static final String QUESTION_TYPE = "questionType";
        public static final String ANSWER_TYPE = "answerType";
        public static final String PARENT_ID = "parentId";
        public static final String FORM_ID = "formId";
        public static final String QUESTION_GROUP_ID = "questionGroupId";
        public static final String QUESTION_ID = "questionId";
        public static final String FORM_INSTANCE_ID = "formInstanceId";
        public static final String ANSWER_ID = "answerId";
        public static final String DATA_POINT_ID = "dataPointId";
        public static final String SUBMITTER_NAME = "submitterName";
        public static final String COLLECTION_DATE = "collectionDate";
        public static final String SURVEYAL_TIME = "surveyalTime";
        public static final String PUBLIC = "public";
        public static final String VALUE = "value";
        public static final String IDENTIFIER = "identifier";
        public static final String SURVEY_GROUP_TYPE = "surveyGroupType";
        public static final String APP_ID = "orgId";
        public static final String URL = "url";
        public static final String ITERATION = "iteration";
    }

    static class Prop {
        public static final String SURVEY_INSTANCE_ID = "surveyInstanceId";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String VALUE_TEXT = "valueText";
        public static final String SURVEY_ID = "surveyId";
        public static final String SURVEYED_LOCALE_ID = "surveyedLocaleId";
        public static final String COLLECTION_DATE = "collectionDate";
        public static final String SURVEYAL_TIME = "surveyalTime";
        public static final String IDENTIFIER = "identifier";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String DISPLAY_NAME = "displayName";
        public static final String NAME = "name";
        public static final String PARENT_ID = "parentId";
        public static final String DESCRIPTION = "description";
        public static final String PRIVACY_LEVEL = "privacyLevel";
        public static final String DESC = "desc";
        public static final String SURVEY_GROUP_ID = "surveyGroupId";
        public static final String ORDER = "order";
        public static final String TEXT = "text";
        public static final String QUESTION_ID = "questionID";
        public static final String QUESTION_IDENTIFIER = "questionId";
        public static final String QUESTION_GROUP_ID = "questionGroupId";
        public static final String PROJECT_TYPE = "projectType";
        public static final String LAST_UPDATE_DATE_TIME = "lastUpdateDateTime";
        public static final String CREATED_DATE_TIME = "createdDateTime";
        public static final String ALIAS = "alias";
        public static final String EVENT_NOTIFICATION = "eventNotification";
        public static final String ENABLE_CHANGE_EVENTS = "enableChangeEvents";
        public static final String ITERATION = "iteration";
    }

    public static final String SURVEY_GROUP_TYPE_SURVEY = "SURVEY";
    public static final String SURVEY_GROUP_TYPE_FOLDER = "FOLDER";

    public static class EventTypes {
        public final EntityType type;
        public final String action;

        public EventTypes(EntityType type, String action) {
            this.type = type;
            this.action = action;
        }
    }

    public static EventTypes getEventAndActionType(String kindName) {
        switch (kindName) {
            case Kind.ANSWER:
                return new EventTypes(EntityType.ANSWER, Action.ANSWER);
            case Kind.FORM_INSTANCE:
                return new EventTypes(EntityType.FORM_INSTANCE, Action.FORM_INSTANCE);
            case Kind.DATA_POINT:
                return new EventTypes(EntityType.DATA_POINT, Action.DATA_POINT);
            case Kind.SURVEY_GROUP:
                return new EventTypes(EntityType.SURVEY_GROUP, Action.SURVEY_GROUP);
            case Kind.FORM:
                return new EventTypes(EntityType.FORM, Action.FORM);
            case Kind.QUESTION_GROUP:
                return new EventTypes(EntityType.QUESTION_GROUP, Action.QUESTION_GROUP);
            case Kind.QUESTION:
                return new EventTypes(EntityType.QUESTION, Action.QUESTION);
            case Kind.DEVICE_FILE:
                return new EventTypes(EntityType.DEVICE_FILE, Action.DEVICE_FILE);
        }
        return null;
    }

    private static Map<String, Object> addProperty(String key, Object val,
            Map<String, Object> data) {
        if (val != null) {
            data.put(key, val);
        }
        return data;
    }

    private static Map<String, Object> addProperty(String key, Object val,
            Object defaultVal, Map<String, Object> data) {
        if (val != null) {
            data.put(key, val);
        } else {
            data.put(key, defaultVal);
        }
        return data;
    }

    public static Map<String, Object> populateEntityProperties(EntityType type, Entity e,
            Map<String, Object> data) {
        switch (type) {
            case ANSWER:
                addProperty(Key.FORM_INSTANCE_ID, e.getProperty(Prop.SURVEY_INSTANCE_ID), data);
                addProperty(Key.ANSWER_TYPE, e.getProperty(Prop.TYPE), data);
                addProperty(Key.QUESTION_ID,
                        Long.valueOf((String) e.getProperty(Prop.QUESTION_ID)), data);

                Object value = e.getProperty(Prop.VALUE);
                if (value == null) {
                    Object text = e.getProperty(Prop.VALUE_TEXT);
                    if (text != null && text instanceof Text) {
                        value = ((Text) text).getValue();
                    }
                }

                addProperty(Key.VALUE, value, data);
                addProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                addProperty(Key.ITERATION, e.getProperty(Prop.ITERATION), data);
                break;
            case FORM_INSTANCE:
                addProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                addProperty(Key.DATA_POINT_ID, e.getProperty(Prop.SURVEYED_LOCALE_ID), data);
                addProperty(Key.COLLECTION_DATE, e.getProperty(Prop.COLLECTION_DATE), data);
                addProperty(Key.SURVEYAL_TIME, e.getProperty(Prop.SURVEYAL_TIME), data);
                break;
            case DATA_POINT:
                addProperty(Key.IDENTIFIER, e.getProperty(Prop.IDENTIFIER), data);
                addProperty(Key.LAT, e.getProperty(Prop.LATITUDE), data);
                addProperty(Key.LON, e.getProperty(Prop.LONGITUDE), data);
                addProperty(Key.NAME, e.getProperty(Prop.DISPLAY_NAME), data);
                addProperty(Key.SURVEY_ID, e.getProperty(Prop.SURVEY_GROUP_ID), data);
                break;
            case SURVEY_GROUP:
                addProperty(Key.NAME, e.getProperty(Prop.NAME), "<name missing>", data);
                addProperty(Key.PARENT_ID, e.getProperty(Prop.PARENT_ID), data);

                if (e.getProperty(Prop.PROJECT_TYPE).toString()
                        .equals(SurveyGroup.ProjectType.PROJECT.toString())) {
                    data.put(Key.SURVEY_GROUP_TYPE, SURVEY_GROUP_TYPE_SURVEY);
                } else if (e.getProperty(Prop.PROJECT_TYPE).toString()
                        .equals(SurveyGroup.ProjectType.PROJECT_FOLDER.toString())) {
                    data.put(Key.SURVEY_GROUP_TYPE, SURVEY_GROUP_TYPE_FOLDER);
                }

                addProperty(Key.DESCRIPTION, e.getProperty(Prop.DESCRIPTION), data);
                addProperty(Key.PUBLIC, String.valueOf(e.getProperty(Prop.PRIVACY_LEVEL))
                        .equals(PrivacyLevel.PUBLIC.toString()), data);
                break;
            case FORM:
                addProperty(Key.NAME, e.getProperty(Prop.NAME), data);
                addProperty(Key.DESCRIPTION, e.getProperty(Prop.DESC), data);
                addProperty(Key.SURVEY_ID, e.getProperty(Prop.SURVEY_GROUP_ID), data);
                break;
            case QUESTION_GROUP:
                addProperty(Key.NAME, e.getProperty(Prop.NAME), "<name missing>", data);
                addProperty(Key.ORDER, e.getProperty(Prop.ORDER), data);
                addProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                break;
            case QUESTION:
                addProperty(Key.DISPLAY_TEXT, e.getProperty(Prop.TEXT), data);
                addProperty(Key.IDENTIFIER, e.getProperty(Prop.QUESTION_IDENTIFIER), data);
                addProperty(Key.QUESTION_GROUP_ID, e.getProperty(Prop.QUESTION_GROUP_ID),
                        data);
                addProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                addProperty(Key.QUESTION_TYPE, e.getProperty(Prop.TYPE), data);
                break;
            case DEVICE_FILE:
                // FIXME move those keys to the proper place
                addProperty("uri", e.getProperty("URI"), data);
                addProperty("checksum", e.getProperty("checksum"), data);
                addProperty("phoneNumber", e.getProperty("phoneNumber"), data);
                addProperty("imei", e.getProperty("imei"), data);
                break;
        }
        return data;
    }

    public static Map<String, Object> newEvent(String orgId, String eventType,
            Map<String, Object> entity,
            Map<String, Object> context) throws AssertionError {

        assert orgId != null : "orgId is required";
        assert eventType != null : "eventType is required";
        assert entity != null : "entity is required";
        assert context != null : "context is required";

        Map<String, Object> result = new HashMap<String, Object>();

        result.put("orgId", orgId);
        result.put("eventType", eventType);
        result.put("entity", entity);
        result.put("context", context);

        return result;
    }

    public static Map<String, Object> newSource(Object principal) {
        Map<String, Object> source = new HashMap<String, Object>();

        if (principal instanceof String) {
            // Tasks related events get an "anonymousUser" as principal
            source.put(Key.TYPE, EventSourceType.SYSTEM);
        } else if (principal instanceof GaeUser) {
            GaeUser usr = (GaeUser) principal;
            source.put(Key.TYPE, EventSourceType.USER);
            source.put(Key.EMAIL, usr.getEmail());
            source.put(Key.ID, usr.getUserId());
        } else {
            log.log(Level.WARNING, "Unable to identify source from authentication principal: "
                    + principal.toString());
        }

        return source;
    }

    public static Map<String, Object> newContext(Date timestamp, Map<String, Object> source) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(Key.TIMESTAMP, timestamp);
        context.put(Key.SOURCE, source);
        return context;

    }

    public static Map<String, Object> newEntity(EntityType type, Long id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(Key.TYPE, type);
        entity.put(Key.ID, id);
        return entity;
    }

    public static void sendEvents(String urlString, List<Map<String, Object>> events)
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "application/json");

        FlowJsonObjectWriter writer = new FlowJsonObjectWriter();
        writer.writeValue(connection.getOutputStream(), events);

        System.out.println("    " + connection.getResponseCode());

        connection.disconnect();
    }
}
