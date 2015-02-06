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

package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.events.Event;
import org.akvo.flow.events.EventContext;
import org.akvo.flow.events.EventEntity;
import org.akvo.flow.events.EventEntity.EventType;
import org.akvo.flow.events.EventSource;
import org.akvo.flow.events.EventSource.EventSourceType;
import org.akvo.flow.events.UnifiedLog;
import org.akvo.flow.events.UnifiedLog.EventTopic;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.PrivacyLevel;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

/**
 * RESTful servlet that is used to send log data to the unified log
 */
public class EventRestServlet extends AbstractRestApiServlet {
    private static final long serialVersionUID = 5923399458369692813L;
    private static final Logger log = Logger
            .getLogger(EventRestServlet.class.getName());
    
    private String action;
    
    // names of kinds in Google App Engine
    public static final String SURVEY_KIND = "SurveyGroup";
    public static final String FORM_KIND = "Survey";
    public static final String QUESTION_GROUP_KIND = "QuestionGroup";
    public static final String QUESTION_KIND = "Question";
    public static final String DATAPOINT_KIND = "SurveyedLocale";
    public static final String FORM_INSTANCE_KIND = "SurveyInstance";
    public static final String ANSWER_KIND = "QuestionAnswerStore";
    
    // How we name the actions
    public static final String SURVEY_ACTION = "survey";
    public static final String FORM_ACTION = "form";
    public static final String QUESTION_GROUP_ACTION = "questionGroup";
    public static final String QUESTION_ACTION = "question";
    public static final String DATAPOINT_ACTION = "dataPoint";
    public static final String FORM_INSTANCE_ACTION = "formInstance";
    public static final String ANSWER_ACTION = "answer";

    // property keys
    public static final String ID_KEY = "id";
    public static final String DISPLAY_TEXT_KEY = "displayText";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String TYPE_KEY = "type";
    public static final String ORDER_KEY = "order";
    public static final String LAT_KEY = "lat";
    public static final String LON_KEY = "lon";
    public static final String ORG_ID_KEY = "orgId";
    public static final String SURVEY_ID_KEY = "surveyId";
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

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new EventRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse resp = new RestResponse();
        EventRestRequest eReq = (EventRestRequest) req;
        try {
            ingestEvent(eReq);
        } catch (RuntimeException | ClassNotFoundException e) {
            log.log(Level.SEVERE, "Could not process "
            		+ eReq.getKind() + " event with id: "
                    + eReq.getId() + ", " + e.getMessage());
        }
        return resp;
    }

    private static class Names {
        EventType type = null;
        String action = null;
    }

    private Names getEventAndActionType(String kindName) {
        Names names = new Names();
        switch (kindName) {
            case ANSWER_KIND:
                names.action = ANSWER_ACTION;
                names.type = EventType.ANSWER;
                break;
            case FORM_INSTANCE_KIND:
                names.action = FORM_INSTANCE_ACTION;
                names.type = EventType.FORM_INSTANCE;
                break;
            case DATAPOINT_KIND:
                names.action = DATAPOINT_ACTION;
                names.type = EventType.DATAPOINT;
                break;
            case SURVEY_KIND:
                names.action = SURVEY_ACTION;
                names.type = EventType.SURVEY;
                break;
            case FORM_KIND:
                names.action = FORM_ACTION;
                names.type = EventType.FORM;
                break;
            case QUESTION_GROUP_KIND:
                names.action = QUESTION_GROUP_ACTION;
                names.type = EventType.QUESTION_GROUP;
                break;
            case QUESTION_KIND:
                names.action = QUESTION_ACTION;
                names.type = EventType.QUESTION;
                break;
        }
        return names;
    }

    private Map<String, Object> createEntityData(EventType type, Object o) {
        Map<String,Object> data = new HashMap<String,Object>();
        switch (type){
            case ANSWER:
                QuestionAnswerStore qa = (QuestionAnswerStore) o;
                data.put(FORM_INSTANCE_ID_KEY, qa.getSurveyInstanceId());
                data.put(TYPE_KEY, qa.getType());
                data.put(VALUE_KEY, qa.getValue());
                break;
            case FORM_INSTANCE:
                SurveyInstance si = (SurveyInstance) o;
                data.put(FORM_ID_KEY, si.getSurveyId());
                data.put(DATAPOINT_ID_KEY, si.getSurveyedLocaleId());
                data.put(COLLECTION_DATE_KEY, si.getCollectionDate());
                data.put(SURVEYAL_TIME_KEY, si.getSurveyalTime());
                break;
            case DATAPOINT:
                SurveyedLocale sl = (SurveyedLocale) o;
                data.put(IDENTIFIER_KEY, sl.getIdentifier());
                data.put(LAT_KEY, sl.getLatitude());
                data.put(LON_KEY, sl.getLongitude());
                data.put(NAME_KEY, sl.getDisplayName());
                break;
            case SURVEY:
                SurveyGroup sg = (SurveyGroup) o;
                data.put(NAME_KEY, sg.getName());
                data.put(DESCRIPTION_KEY, sg.getDescription());
                data.put(PUBLIC_KEY, sg.getPrivacyLevel() == PrivacyLevel.PUBLIC);
                break;
            case FORM:
                Survey s = (Survey) o;
                data.put(NAME_KEY, s.getName());
                data.put(DESCRIPTION_KEY, s.getDesc());
                data.put(SURVEY_ID_KEY, s.getSurveyGroupId());
                break;
            case QUESTION_GROUP:
                QuestionGroup qg = (QuestionGroup) o;
                data.put(NAME_KEY, qg.getName());
                data.put(ORDER_KEY, qg.getOrder());
                break;
            case QUESTION:
                Question q = (Question) o;
                data.put(DISPLAY_TEXT_KEY, q.getText());
                data.put(IDENTIFIER_KEY, q.getQuestionId());
                data.put(QUESTION_GROUP_ID_KEY, q.getQuestionGroupId());
                data.put(FORM_ID_KEY, q.getSurveyId());
                data.put(TYPE_KEY, q.getType());
                break;
        }
        return data;
    }
    
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
	private void ingestEvent(EventRestRequest eReq) throws ClassNotFoundException {
    	Class<?> kindClass = Class.forName(eReq.getKind());

    	if (kindClass != null){
    		// extract name of kind from class name (strip of first part of fully qualified name)
			String kindName = eReq.getKind().substring(eReq.getKind().lastIndexOf('.') + 1);
			
			// populate action name and entity type
			Names names = getEventAndActionType(kindName);

			// if this is not a delete action, get the object by key
			Object o = null;
            BaseDAO<?> dao  = new BaseDAO(kindClass);
    		Boolean deleted = eReq.getActionType().equals(EventRestRequest.ACTION_DELETED);
    		if (!deleted){
    			o = dao.getByKey(eReq.getId());
    		}

    		if (o != null || deleted){
    		    // create new event and populate organisation id and action type
    		    Event newEvent = new Event(eReq.getOrgId(),names.action + eReq.getActionType());
    		    
    		    // create new entity and populate its properties based on the type
    		    EventEntity entity = new EventEntity(names.type,eReq.getId());
    			Map<String,Object> data = null;
    			if (!deleted){
    			    data = createEntityData(names.type,o);
    			    entity.setData(data);
    			}
                newEvent.setEntity(entity);

                // create new context and populate its properties
                EventContext context = new EventContext(eReq.getTimestamp());
                // create new eventSource
                EventSource source = new EventSource(EventSourceType.USER, eReq.getUserId());
                context.setSource(source);
                newEvent.setContext(context);

                // create json string
    			ObjectMapper om = new ObjectMapper();
    	    	try {
    				String eventString = om.writeValueAsString(newEvent);
                    UnifiedLog.dispatch(EventTopic.DATA, eventString);
    			} catch (IOException e) {
                    log.log(Level.SEVERE, "Could not process event for"
                            + eReq.getKind() + " event with id: "
                            + eReq.getId() + ", " + e.getMessage());
    				e.printStackTrace();
    			}
    		}
    	}
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }
}