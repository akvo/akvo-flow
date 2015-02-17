
package org.akvo.flow.events;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.PrivacyLevel;
import com.google.appengine.api.datastore.Entity;

public class EventUtils {

    public enum EventSourceType {
        USER, DEVICE, SENSOR, WEBFORM, API
    };

    public enum EventType {
        SURVEY_GROUP, FORM, QUESTION_GROUP, QUESTION, DATAPOINT, FORM_INSTANCE, ANSWER
    };

    // names of kinds in Google App Engine
    public static final String SURVEY_GROUP_KIND = "SurveyGroup";
    public static final String FORM_KIND = "Survey";
    public static final String QUESTION_GROUP_KIND = "QuestionGroup";
    public static final String QUESTION_KIND = "Question";
    public static final String DATAPOINT_KIND = "SurveyedLocale";
    public static final String FORM_INSTANCE_KIND = "SurveyInstance";
    public static final String ANSWER_KIND = "QuestionAnswerStore";

    // How we name the actions
    public static final String SURVEY_GROUP_ACTION = "surveyGroup";
    public static final String FORM_ACTION = "form";
    public static final String QUESTION_GROUP_ACTION = "questionGroup";
    public static final String QUESTION_ACTION = "question";
    public static final String DATAPOINT_ACTION = "dataPoint";
    public static final String FORM_INSTANCE_ACTION = "formInstance";
    public static final String ANSWER_ACTION = "answer";

    // property keys in events
    public static final String ID_KEY = "id";
    public static final String EMAIL_KEY = "email";
    public static final String DISPLAY_TEXT_KEY = "displayText";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String SOURCE_KEY = "source";
    public static final String TYPE_KEY = "type";
    public static final String ORDER_KEY = "order";
    public static final String LAT_KEY = "lat";
    public static final String LON_KEY = "lon";
    public static final String ORG_ID_KEY = "orgId";
    public static final String SURVEY_ID_KEY = "surveyId";
    public static final String QUESTION_TYPE_KEY = "questionType";
    public static final String PARENT_ID_KEY = "parentId";
    public static final String FORM_ID_KEY = "formId";
    public static final String QUESTION_GROUP_ID_KEY = "questionGroupId";
    public static final String QUESTION_ID_KEY = "questionId";
    public static final String FORM_INSTANCE_ID_KEY = "formInstanceId";
    public static final String ANSWER_ID_KEY = "answerId";
    public static final String DATAPOINT_ID_KEY = "dataPointId";
    public static final String SUBMITTER_NAME_KEY = "submitterName";
    public static final String COLLECTION_DATE_KEY = "collectionDate";
    public static final String SURVEYAL_TIME_KEY = "surveyalTime";
    public static final String PUBLIC_KEY = "public";
    public static final String VALUE_KEY = "value";
    public static final String IDENTIFIER_KEY = "identifier";
    public static final String SURVEY_GROUP_TYPE_KEY = "surveyGroupType";

    // properties in putContext
    public static final String SURVEY_INSTANCE_ID_PROP = "surveyInstanceId";
    public static final String TYPE_PROP = "type";
    public static final String VALUE_PROP = "value";
    public static final String SURVEY_ID_PROP = "surveyId";
    public static final String SURVEYED_LOCALE_ID_PROP = "surveyedLocaleId";
    public static final String COLLECTION_DATE_PROP = "collectionDate";
    public static final String SURVEYAL_TIME_PROP = "surveyalTime";
    public static final String IDENTIFIER_PROP = "identifier";
    public static final String LATITUDE_PROP = "latitude";
    public static final String LONGITUDE_PROP = "longitude";
    public static final String DISPLAY_NAME_PROP = "displayName";
    public static final String NAME_PROP = "name";
    public static final String PARENT_ID_PROP = "parentId";
    public static final String DESCRIPTION_PROP = "description";
    public static final String PRIVACY_LEVEL_PROP = "privacyLevel";
    public static final String DESC_PROP = "desc";
    public static final String SURVEY_GROUP_ID_PROP = "surveyGroupId";
    public static final String ORDER_PROP = "order";
    public static final String TEXT_PROP = "text";
    public static final String QUESTION_ID_PROP = "questionId";
    public static final String QUESTION_GROUP_ID_PROP = "questionGroupId";
    public static final String PROJECT_TYPE_PROP = "projectType";

    public static final String ACTION_DELETED = "Deleted";
    public static final String ACTION_CREATED = "Created";
    public static final String ACTION_UPDATED = "Updated";

    public static final String SURVEY_GROUP_TYPE_SURVEY = "SURVEY";
    public static final String SURVEY_GROUP_TYPE_FOLDER = "FOLDER";

    public static class EventTypes {
        EventType type = null;
        String action = null;
    }

    public static EventTypes getEventAndActionType(String kindName) {
        EventTypes names = new EventTypes();
        switch (kindName) {
            case EventUtils.ANSWER_KIND:
                names.action = EventUtils.ANSWER_ACTION;
                names.type = EventType.ANSWER;
                break;
            case EventUtils.FORM_INSTANCE_KIND:
                names.action = EventUtils.FORM_INSTANCE_ACTION;
                names.type = EventType.FORM_INSTANCE;
                break;
            case EventUtils.DATAPOINT_KIND:
                names.action = EventUtils.DATAPOINT_ACTION;
                names.type = EventType.DATAPOINT;
                break;
            case EventUtils.SURVEY_GROUP_KIND:
                names.action = EventUtils.SURVEY_GROUP_ACTION;
                names.type = EventType.SURVEY_GROUP;
                break;
            case EventUtils.FORM_KIND:
                names.action = EventUtils.FORM_ACTION;
                names.type = EventType.FORM;
                break;
            case EventUtils.QUESTION_GROUP_KIND:
                names.action = EventUtils.QUESTION_GROUP_ACTION;
                names.type = EventType.QUESTION_GROUP;
                break;
            case EventUtils.QUESTION_KIND:
                names.action = EventUtils.QUESTION_ACTION;
                names.type = EventType.QUESTION;
                break;
        }
        return names;
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
                addNonNullProperty(FORM_INSTANCE_ID_KEY, e.getProperty(SURVEY_INSTANCE_ID_PROP),
                        data);
                addNonNullProperty(TYPE_KEY, e.getProperty(TYPE_PROP), data);
                addNonNullProperty(VALUE_KEY, e.getProperty(VALUE_PROP), data);
                break;
            case FORM_INSTANCE:
                addNonNullProperty(FORM_ID_KEY, e.getProperty(SURVEY_ID_PROP), data);
                addNonNullProperty(DATAPOINT_ID_KEY, e.getProperty(SURVEYED_LOCALE_ID_PROP), data);
                addNonNullProperty(COLLECTION_DATE_KEY, e.getProperty(COLLECTION_DATE_PROP), data);
                addNonNullProperty(SURVEYAL_TIME_KEY, e.getProperty(SURVEYAL_TIME_PROP), data);
                break;
            case DATAPOINT:
                addNonNullProperty(IDENTIFIER_KEY, e.getProperty(IDENTIFIER_PROP), data);
                addNonNullProperty(LAT_KEY, e.getProperty(LATITUDE_PROP), data);
                addNonNullProperty(LON_KEY, e.getProperty(LONGITUDE_PROP), data);
                addNonNullProperty(NAME_KEY, e.getProperty(DISPLAY_NAME_PROP), data);
                break;
            case SURVEY_GROUP:
                addNonNullProperty(NAME_KEY, e.getProperty(NAME_PROP), data);
                addNonNullProperty(PARENT_ID_KEY, e.getProperty(PARENT_ID_PROP), data);
                if (e.getProperty(PROJECT_TYPE_PROP).toString()
                        .equals(SurveyGroup.ProjectType.PROJECT.toString())) {
                    data.put(SURVEY_GROUP_TYPE_KEY, SURVEY_GROUP_TYPE_SURVEY);
                } else if (e.getProperty(PROJECT_TYPE_PROP).toString()
                        .equals(SurveyGroup.ProjectType.PROJECT_FOLDER.toString())) {
                    data.put(SURVEY_GROUP_TYPE_KEY, SURVEY_GROUP_TYPE_FOLDER);
                }
                addNonNullProperty(DESCRIPTION_KEY, e.getProperty(DESCRIPTION_PROP), data);
                addNonNullProperty(PUBLIC_KEY,
                        e.getProperty(PRIVACY_LEVEL_PROP).toString()
                                .equals(PrivacyLevel.PUBLIC.toString()), data);
                break;
            case FORM:
                addNonNullProperty(NAME_KEY, e.getProperty(NAME_PROP), data);
                addNonNullProperty(DESCRIPTION_KEY, e.getProperty(DESC_PROP), data);
                addNonNullProperty(SURVEY_ID_KEY, e.getProperty(SURVEY_GROUP_ID_PROP), data);
                break;
            case QUESTION_GROUP:
                addNonNullProperty(NAME_KEY, e.getProperty(NAME_PROP), data);
                addNonNullProperty(ORDER_KEY, e.getProperty(ORDER_PROP), data);
                addNonNullProperty(FORM_ID_KEY, e.getProperty(SURVEY_ID_PROP), data);
                break;
            case QUESTION:
                addNonNullProperty(DISPLAY_TEXT_KEY, e.getProperty(TEXT_PROP), data);
                addNonNullProperty(IDENTIFIER_KEY, e.getProperty(QUESTION_ID_PROP), data);
                addNonNullProperty(QUESTION_GROUP_ID_KEY, e.getProperty(QUESTION_GROUP_ID_PROP),
                        data);
                addNonNullProperty(FORM_ID_KEY, e.getProperty(SURVEY_ID_PROP), data);
                addNonNullProperty(QUESTION_TYPE_KEY, e.getProperty(TYPE_PROP), data);
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

    public static Map<String, Object> newSource(EventSourceType sourceType, String cred) {
        Map<String, Object> source = new HashMap<String, Object>();
        source.put(TYPE_KEY, sourceType);
        source.put(EMAIL_KEY, cred);
        return source;
    }

    public static Map<String, Object> newContext(Date timestamp, Map<String, Object> source) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(TIMESTAMP_KEY, timestamp);
        context.put(SOURCE_KEY, source);
        return context;

    }

    public static Map<String, Object> newEntity(EventType type, Long id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(TYPE_KEY, type);
        entity.put(ID_KEY, id);
        return entity;
    }
}
