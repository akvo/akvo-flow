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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.events.EventQueue;
import org.akvo.flow.events.EventUtils;
import org.akvo.flow.events.EventUtils.EventSourceType;
import org.akvo.flow.events.EventUtils.EventType;
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
import com.gallatinsystems.survey.domain.SurveyGroup.ProjectType;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

/**
 * RESTful servlet that is used to send log data to the unified log
 */
public class EventRestServlet extends AbstractRestApiServlet {
    private static final long serialVersionUID = 5923399458369692813L;
    private static final Logger log = Logger
            .getLogger(EventRestServlet.class.getName());
    
    private String action;
    



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

    private Names getEventAndActionType(String kindName, ProjectType surveyGroupType) {
        Names names = new Names();
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
            // there are two types with SURVEY_KIND (surveygroup):
            // folders and surveys. We need to split them here
            case EventUtils.SURVEY_KIND:
                if (surveyGroupType == ProjectType.PROJECT) {
                    names.action = EventUtils.SURVEY_ACTION;
                    names.type = EventType.SURVEY;
                } else if (surveyGroupType == ProjectType.PROJECT_FOLDER) {
                    names.action = EventUtils.FOLDER_ACTION;
                    names.type = EventType.FOLDER;
                } else {
                    names.action = EventUtils.UNKNOWN_ACTION;
                    names.type = EventType.UNKNOWN;
                }
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
            default:
                names.action = EventUtils.UNKNOWN_ACTION;
                names.type = EventType.UNKNOWN;
                break;
        }
        return names;
    }

    private Map<String, Object> populateEntityProperties(EventType type, Object o,
            Map<String, Object> data) {
        switch (type){
            case ANSWER:
                QuestionAnswerStore qa = (QuestionAnswerStore) o;
                data.put(EventUtils.FORM_INSTANCE_ID_KEY, qa.getSurveyInstanceId());
                data.put(EventUtils.TYPE_KEY, qa.getType());
                data.put(EventUtils.VALUE_KEY, qa.getValue());
                break;
            case FORM_INSTANCE:
                SurveyInstance si = (SurveyInstance) o;
                data.put(EventUtils.FORM_ID_KEY, si.getSurveyId());
                data.put(EventUtils.DATAPOINT_ID_KEY, si.getSurveyedLocaleId());
                data.put(EventUtils.COLLECTION_DATE_KEY, si.getCollectionDate());
                data.put(EventUtils.SURVEYAL_TIME_KEY, si.getSurveyalTime());
                break;
            case DATAPOINT:
                SurveyedLocale sl = (SurveyedLocale) o;
                data.put(EventUtils.IDENTIFIER_KEY, sl.getIdentifier());
                data.put(EventUtils.LAT_KEY, sl.getLatitude());
                data.put(EventUtils.LON_KEY, sl.getLongitude());
                data.put(EventUtils.NAME_KEY, sl.getDisplayName());
                break;
            case FOLDER:
                SurveyGroup sgf = (SurveyGroup) o;
                data.put(EventUtils.NAME_KEY, sgf.getName());
                data.put(EventUtils.PARENT_ID_KEY, sgf.getParentId());
                break;
            case SURVEY:
                SurveyGroup sg = (SurveyGroup) o;
                data.put(EventUtils.NAME_KEY, sg.getName());
                data.put(EventUtils.DESCRIPTION_KEY, sg.getDescription());
                data.put(EventUtils.PUBLIC_KEY, sg.getPrivacyLevel() == PrivacyLevel.PUBLIC);
                break;
            case FORM:
                Survey s = (Survey) o;
                data.put(EventUtils.NAME_KEY, s.getName());
                data.put(EventUtils.DESCRIPTION_KEY, s.getDesc());
                data.put(EventUtils.SURVEY_ID_KEY, s.getSurveyGroupId());
                break;
            case QUESTION_GROUP:
                QuestionGroup qg = (QuestionGroup) o;
                data.put(EventUtils.NAME_KEY, qg.getName());
                data.put(EventUtils.ORDER_KEY, qg.getOrder());
                data.put(EventUtils.FORM_ID_KEY, qg.getSurveyId());
                break;
            case QUESTION:
                Question q = (Question) o;
                data.put(EventUtils.DISPLAY_TEXT_KEY, q.getText());
                if (q.getQuestionId() != null) {
                    data.put(EventUtils.IDENTIFIER_KEY, q.getQuestionId());
                }
                data.put(EventUtils.QUESTION_GROUP_ID_KEY, q.getQuestionGroupId());
                data.put(EventUtils.FORM_ID_KEY, q.getSurveyId());
                data.put(EventUtils.QUESTION_TYPE_KEY, q.getType());
                break;
        }
        return data;
    }
    
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private void ingestEvent(EventRestRequest eReq) throws ClassNotFoundException {
        Class<?> kindClass = Class.forName(eReq.getKind());

        if (kindClass != null) {
            // extract name of kind from class name (strip of first part of fully qualified name)
            String kindName = eReq.getKind().substring(eReq.getKind().lastIndexOf('.') + 1);

            Object o = null;
            BaseDAO<?> dao  = new BaseDAO(kindClass);
            Boolean deleted = eReq.getActionType().equals(EventRestRequest.ACTION_DELETED);
            if (!deleted) {
                o = dao.getByKey(eReq.getId());
            }

            // populate action name and entity type
            Names names = getEventAndActionType(kindName, eReq.getSurveyGroupType());

            // if this is not a delete action, get the object by key
            if (o != null || deleted) {
                // create source map
                Map<String, Object> source = EventUtils.newSource(EventSourceType.USER,
                        eReq.getUserId());

                // create context map
                Map<String, Object> context = EventUtils.newContext(eReq.getTimestamp(), source);

                // create entity map
                Map<String, Object> entity = EventUtils.newEntity(names.type, eReq.getId());
                if (!deleted) {
                    populateEntityProperties(names.type, o, entity);
                }
                
                // create event map
                Map<String, Object> event = null;
                try {
                    event = EventUtils.newEvent(eReq.getOrgId(), names.action
                        + eReq.getActionType(), entity, context);
                } catch (AssertionError e) {
                    log.log(Level.SEVERE, "Could not process event for"
                            + eReq.getKind() + " event with id: "
                            + eReq.getId() + ", " + e.getMessage());
                    e.printStackTrace();
                }

                // serialize event map
                ObjectMapper om = new ObjectMapper();
                String eventString = null;
                try {
                    eventString = om.writeValueAsString(event);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Could not process event for"
                            + eReq.getKind() + " event with id: "
                            + eReq.getId() + ", " + e.getMessage());
                    e.printStackTrace();
                }

                // check validity
                // log.log(Level.INFO, "event: " + eventString);
                // Boolean validationResult = false;
                // try {
                // String schemaUri = new File(
                // "https://raw.githubusercontent.com/akvo/akvo-core-services/unified-log/flow-data-schema/schema/event.json")
                // .toURI().toString();
                // JsonSchema schema = JsonSchemaFactory
                // .byDefault()
                // .getJsonSchema(schemaUri);
                // validationResult = schema.validInstance(om.readValue(eventString,
                // JsonNode.class));
                // log.log(Level.INFO, "validation of event: " + validationResult.toString());
                // } catch (Exception e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }

                // store in datastore.
                BaseDAO<EventQueue> eventDao = new BaseDAO(EventQueue.class);
                EventQueue eventQ = new EventQueue(eReq.getTimestamp(), eventString);
                eventDao.save(eventQ);

                // UnifiedLog.dispatch(EventTopic.DATA, eventString);/
            }
        }
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }
}