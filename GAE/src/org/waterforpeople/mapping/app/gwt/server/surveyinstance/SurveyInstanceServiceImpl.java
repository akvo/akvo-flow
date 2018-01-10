/*
 *  Copyright (C) 2010-2018 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import org.apache.log4j.Logger;
import org.waterforpeople.mapping.domain.SurveyInstance;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Deprecated
public class SurveyInstanceServiceImpl {

    private static final Logger log = Logger
            .getLogger(SurveyInstanceServiceImpl.class);

    public void sendProcessingMessages(SurveyInstance domain) {
        // send async request to populate the AccessPoint using the mapping
        QueueFactory.getDefaultQueue().add(
                TaskOptions.Builder.withUrl("/app_worker/task")
                        .param("action", "addAccessPoint")
                        .param("surveyId", domain.getKey().getId() + ""));
        // send asyn crequest to summarize the instance
        QueueFactory.getQueue("dataSummarization").add(
                TaskOptions.Builder.withUrl("/app_worker/datasummarization")
                        .param("objectKey", domain.getKey().getId() + "")
                        .param("type", "SurveyInstance"));
        QueueFactory.getDefaultQueue().add(
                TaskOptions.Builder
                        .withUrl("/app_worker/surveyalservlet")
                        .param(SurveyalRestRequest.ACTION_PARAM,
                                SurveyalRestRequest.INGEST_INSTANCE_ACTION)
                        .param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
                                domain.getKey().getId() + ""));
    }

}
