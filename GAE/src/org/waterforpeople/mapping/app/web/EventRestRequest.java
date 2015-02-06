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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * request class for the EventServlet.
 * 
 */
public class EventRestRequest extends RestRequest {

    private static final long serialVersionUID = -1002622416183902696L;
    public static final String KIND_PARAM = "kind";
    public static final String ID_PARAM = "id";
    public static final String ACTION_TYPE_PARAM = "actionType";
    public static final String USER_ID_PARAM = "userId";
    public static final String ORG_ID_PARAM = "orgId";
    public static final String TIMESTAMP_PARAM = "timestamp";
    public static final String ACTION_DELETED = "Deleted";
    public static final String ACTION_CREATED = "Created";
    public static final String ACTION_UPDATED = "Updated";

    //list of handled classes.
    public static final List<String> HANDLED_EVENTS = Arrays.asList(
    		"com.gallatinsystems.survey.domain.SurveyGroup",
    		"com.gallatinsystems.survey.domain.Survey",
    		"com.gallatinsystems.survey.domain.QuestionGroup",
    		"com.gallatinsystems.survey.domain.Question",
    		"com.gallatinsystems.survey.domain.SurveyInstance",
    		"com.gallatinsystems.survey.domain.QuestionAnswerStore",
    		"com.gallatinsystems.survey.domain.SurveyedLocale");

    private String kind;
    private String actionType;
    private Long userId;
    private Long id;
    private String orgId;
    private Date timestamp;
    
    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
    	
    	if (req.getParameter(KIND_PARAM) != null) {
    		setKind(req.getParameter(KIND_PARAM));
    	}
    	
    	if (req.getParameter(ACTION_TYPE_PARAM) != null) {
    		setActionType(req.getParameter(ACTION_TYPE_PARAM));
    	}

    	if (req.getParameter(USER_ID_PARAM) != null) {
    		try {
                setUserId(Long.parseLong(req.getParameter(USER_ID_PARAM).trim()));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, USER_ID_PARAM
                                + " must be a Long"));
            }
    	}
    	
    	if (req.getParameter(ID_PARAM) != null) {
    		try {
                setId(Long.parseLong(req.getParameter(ID_PARAM).trim()));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, ID_PARAM
                                + " must be a Long"));
            }
    	}

    	if (req.getParameter(ORG_ID_PARAM) != null) {
    		setOrgId(req.getParameter(ORG_ID_PARAM));
    	}

    	if (req.getParameter(TIMESTAMP_PARAM) != null) {
    		try {
                Long unixTimeStamp = Long.parseLong(req.getParameter(TIMESTAMP_PARAM).trim());
                setTimestamp(new Date(unixTimeStamp));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, TIMESTAMP_PARAM
                                + " must be a Long"));
            }
    	}
    }

    @Override
    protected void populateErrors() {

    }

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
