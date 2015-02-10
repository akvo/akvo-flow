
package org.akvo.flow.events;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventUtils {

    public enum EventSourceType {
        USER, DEVICE, SENSOR, WEBFORM, API
    };

    public enum EventType {
        FOLDER, SURVEY, FORM, QUESTION_GROUP, QUESTION, DATAPOINT, FORM_INSTANCE, ANSWER, UNKNOWN
    };

    // names of kinds in Google App Engine
    public static final String SURVEY_KIND = "SurveyGroup";
    public static final String FORM_KIND = "Survey";
    public static final String QUESTION_GROUP_KIND = "QuestionGroup";
    public static final String QUESTION_KIND = "Question";
    public static final String DATAPOINT_KIND = "SurveyedLocale";
    public static final String FORM_INSTANCE_KIND = "SurveyInstance";
    public static final String ANSWER_KIND = "QuestionAnswerStore";

    // How we name the actions
    public static final String FOLDER_ACTION = "folder";
    public static final String SURVEY_ACTION = "survey";
    public static final String FORM_ACTION = "form";
    public static final String QUESTION_GROUP_ACTION = "questionGroup";
    public static final String QUESTION_ACTION = "question";
    public static final String DATAPOINT_ACTION = "dataPoint";
    public static final String FORM_INSTANCE_ACTION = "formInstance";
    public static final String ANSWER_ACTION = "answer";
    public static final String UNKNOWN_ACTION = "unknown";

    // property keys
    public static final String ID_KEY = "id";
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

    public static Map<String, Object> newSource(EventSourceType sourceType, Long id) {
        Map<String, Object> source = new HashMap<String, Object>();
        source.put(TYPE_KEY, sourceType);
        source.put(ID_KEY, id);
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
