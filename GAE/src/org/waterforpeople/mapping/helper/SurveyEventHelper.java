/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.helper;

import org.waterforpeople.mapping.app.web.dto.SurveyEventRequest;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Class to abstract firing survey events
 * 
 * @author Christopher Fagiani
 */
public class SurveyEventHelper {

    public static final String SUBMISSION_EVENT = "surveySubmission";
    public static final String APPROVAL_EVENT = "surveyApproval";

    /**
     * generates a task to the event queue
     * 
     * @param eventType
     * @param surveyId
     * @param surveyInstanceId
     */
    public static final void fireEvent(String eventType, Long surveyId,
            Long surveyInstanceId) {
        TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/surveyevent")
                .param(SurveyEventRequest.ACTION_PARAM,
                        SurveyEventRequest.FIRE_EVENT_ACTION)
                .param(SurveyEventRequest.EVENT_TYPE_PARAM, eventType)
                .param(SurveyEventRequest.SURVEY_ID_PARAM, surveyId.toString());
        if (surveyInstanceId != null) {
            options.param(SurveyEventRequest.SURVEY_INSTANCE_ID_PARAM,
                    surveyInstanceId.toString());
        }
        QueueFactory.getDefaultQueue().add(options);
    }

}
