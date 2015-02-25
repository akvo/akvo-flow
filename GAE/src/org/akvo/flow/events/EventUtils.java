
package org.akvo.flow.events;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.web.rest.security.user.GaeUser;

import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.PrivacyLevel;
import com.google.appengine.api.datastore.Entity;

public class EventUtils {

    private static Logger log = Logger.getLogger(EventUtils.class.getName());

    public enum EventSourceType {
        USER, DEVICE, SENSOR, WEBFORM, API, UNKNOWN
    };

    public enum EventType {
        SURVEY_GROUP, FORM, QUESTION_GROUP, QUESTION, DATA_POINT, FORM_INSTANCE, ANSWER
    };

    // names of kinds in Google App Engine
    static class Kind {
        public static final String SURVEY_GROUP = "SurveyGroup";
        public static final String FORM = "Survey";
        public static final String QUESTION_GROUP = "QuestionGroup";
        public static final String QUESTION = "Question";
        public static final String DATAPOINT = "SurveyedLocale";
        public static final String FORM_INSTANCE = "SurveyInstance";
        public static final String ANSWER = "QuestionAnswerStore";
    }

    // How we name the actions
    static class Action {
        public static final String SURVEY_GROUP = "surveyGroup";
        public static final String FORM = "form";
        public static final String QUESTION_GROUP = "questionGroup";
        public static final String QUESTION = "question";
        public static final String DATAPOINT = "dataPoint";
        public static final String FORM_INSTANCE = "formInstance";
        public static final String ANSWER = "answer";

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
        public static final String DATAPOINT_ID = "dataPointId";
        public static final String SUBMITTER_NAME = "submitterName";
        public static final String COLLECTION_DATE = "collectionDate";
        public static final String SURVEYAL_TIME = "surveyalTime";
        public static final String PUBLIC = "public";
        public static final String VALUE = "value";
        public static final String IDENTIFIER = "identifier";
        public static final String SURVEY_GROUP_TYPE = "surveyGroupType";
        public static final String APP_ID = "orgId";
        public static final String ALIAS = "alias";
    }

    static class Prop {
        public static final String SURVEY_INSTANCE_ID = "surveyInstanceId";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
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
        public static final String QUESTION_ID = "questionId";
        public static final String QUESTION_GROUP_ID = "questionGroupId";
        public static final String PROJECT_TYPE = "projectType";
        public static final String LAST_UPDATE_DATE_TIME = "lastUpdateDateTime";
        public static final String CREATED_DATE_TIME = "createdDateTime";
        public static final String ALIAS = "alias";
        public static final String EVENT_NOTIFICATION = "eventNotification";
    }

    public static final String SURVEY_GROUP_TYPE_SURVEY = "SURVEY";
    public static final String SURVEY_GROUP_TYPE_FOLDER = "FOLDER";

    public static class EventTypes {
        public final EventType type;
        public final String action;

        public EventTypes(EventType type, String action) {
            this.type = type;
            this.action = action;
        }
    }

    public static EventTypes getEventAndActionType(String kindName) {
        switch (kindName) {
            case Kind.ANSWER:
                return new EventTypes(EventType.ANSWER, Action.ANSWER);
            case Kind.FORM_INSTANCE:
                return new EventTypes(EventType.FORM_INSTANCE, Action.FORM_INSTANCE);
            case Kind.DATAPOINT:
                return new EventTypes(EventType.DATA_POINT, Action.DATAPOINT);
            case Kind.SURVEY_GROUP:
                return new EventTypes(EventType.SURVEY_GROUP, Action.SURVEY_GROUP);
            case Kind.FORM:
                return new EventTypes(EventType.FORM, Action.FORM);
            case Kind.QUESTION_GROUP:
                return new EventTypes(EventType.QUESTION_GROUP, Action.QUESTION_GROUP);
            case Kind.QUESTION:
                return new EventTypes(EventType.QUESTION, Action.QUESTION);
        }
        return null;
    }

    private static Map<String, Object> addNonNullProperty(String key, Object val,
            Map<String, Object> data) {
        if (val != null) {
            data.put(key, val);
            return data;
        }
        return data;
    }

    public static Map<String, Object> populateEntityProperties(EventType type, Entity e,
            Map<String, Object> data) {
        switch (type) {
            case ANSWER:
                addNonNullProperty(Key.FORM_INSTANCE_ID, e.getProperty(Prop.SURVEY_INSTANCE_ID),
                        data);
                addNonNullProperty(Key.ANSWER_TYPE, e.getProperty(Prop.TYPE), data);
                addNonNullProperty(Key.QUESTION_ID, e.getProperty(Prop.QUESTION_ID), data);
                addNonNullProperty(Key.VALUE, e.getProperty(Prop.VALUE), data);
                break;
            case FORM_INSTANCE:
                addNonNullProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                addNonNullProperty(Key.DATAPOINT_ID, e.getProperty(Prop.SURVEYED_LOCALE_ID), data);
                addNonNullProperty(Key.COLLECTION_DATE, e.getProperty(Prop.COLLECTION_DATE), data);
                addNonNullProperty(Key.SURVEYAL_TIME, e.getProperty(Prop.SURVEYAL_TIME), data);
                break;
            case DATA_POINT:
                addNonNullProperty(Key.IDENTIFIER, e.getProperty(Prop.IDENTIFIER), data);
                addNonNullProperty(Key.LAT, e.getProperty(Prop.LATITUDE), data);
                addNonNullProperty(Key.LON, e.getProperty(Prop.LONGITUDE), data);
                addNonNullProperty(Key.NAME, e.getProperty(Prop.DISPLAY_NAME), data);
                addNonNullProperty(Key.SURVEY_ID, e.getProperty(Prop.SURVEY_GROUP_ID), data);
                break;
            case SURVEY_GROUP:
                addNonNullProperty(Key.NAME, e.getProperty(Prop.NAME), data);
                addNonNullProperty(Key.PARENT_ID, e.getProperty(Prop.PARENT_ID), data);
                if (e.getProperty(Prop.PROJECT_TYPE).toString()
                        .equals(SurveyGroup.ProjectType.PROJECT.toString())) {
                    data.put(Key.SURVEY_GROUP_TYPE, SURVEY_GROUP_TYPE_SURVEY);
                } else if (e.getProperty(Prop.PROJECT_TYPE).toString()
                        .equals(SurveyGroup.ProjectType.PROJECT_FOLDER.toString())) {
                    data.put(Key.SURVEY_GROUP_TYPE, SURVEY_GROUP_TYPE_FOLDER);
                }
                addNonNullProperty(Key.DESCRIPTION, e.getProperty(Prop.DESCRIPTION), data);
                addNonNullProperty(Key.PUBLIC, e.getProperty(Prop.PRIVACY_LEVEL).toString()
                        .equals(PrivacyLevel.PUBLIC.toString()), data);
                break;
            case FORM:
                addNonNullProperty(Key.NAME, e.getProperty(Prop.NAME), data);
                addNonNullProperty(Key.DESCRIPTION, e.getProperty(Prop.DESC), data);
                addNonNullProperty(Key.SURVEY_ID, e.getProperty(Prop.SURVEY_GROUP_ID), data);
                break;
            case QUESTION_GROUP:
                addNonNullProperty(Key.NAME, e.getProperty(Prop.NAME), data);
                addNonNullProperty(Key.ORDER, e.getProperty(Prop.ORDER), data);
                addNonNullProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                break;
            case QUESTION:
                addNonNullProperty(Key.DISPLAY_TEXT, e.getProperty(Prop.TEXT), data);
                addNonNullProperty(Key.IDENTIFIER, e.getProperty(Prop.QUESTION_ID), data);
                addNonNullProperty(Key.QUESTION_GROUP_ID, e.getProperty(Prop.QUESTION_GROUP_ID),
                        data);
                addNonNullProperty(Key.FORM_ID, e.getProperty(Prop.SURVEY_ID), data);
                addNonNullProperty(Key.QUESTION_TYPE, e.getProperty(Prop.TYPE), data);
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
            // FIXME: Is this the right thing to do?
            source.put(Key.TYPE, EventSourceType.DEVICE);
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

    public static Map<String, Object> newEntity(EventType type, Long id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(Key.TYPE, type);
        entity.put(Key.ID, id);
        return entity;
    }
}
