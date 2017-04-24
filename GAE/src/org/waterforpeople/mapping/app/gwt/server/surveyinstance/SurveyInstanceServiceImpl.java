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

package org.waterforpeople.mapping.app.gwt.server.surveyinstance;

import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizationRequest;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@Deprecated
public class SurveyInstanceServiceImpl extends RemoteServiceServlet implements
        SurveyInstanceService {

    private static final long serialVersionUID = -9175237700587455358L;
    private static final Logger log = Logger
            .getLogger(SurveyInstanceServiceImpl.class);

    /**
     * deletes a survey instance. This will only back out Question summaries. To back out the access
     * point, the AP needs to be deleted manually since it may have come from multiple instances.
     */
    @Deprecated
    @Override
    public void deleteSurveyInstance(Long instanceId) {
        if (instanceId != null) {
            SurveyInstanceDAO dao = new SurveyInstanceDAO();
            List<QuestionAnswerStore> answers = dao.listQuestionAnswerStore(
                    instanceId, null);
            if (answers != null) {
                // back out summaries
                Queue queue = QueueFactory.getQueue("dataUpdate");
                for (QuestionAnswerStore ans : answers) {
                    DataChangeRecord value = new DataChangeRecord(
                            QuestionAnswerStore.class.getName(),
                            ans.getQuestionID(), ans.getValue(), "");
                    queue.add(TaskOptions.Builder
                            .withUrl("/app_worker/dataupdate")
                            .param(DataSummarizationRequest.OBJECT_KEY,
                                    ans.getQuestionID())
                            .param(DataSummarizationRequest.OBJECT_TYPE,
                                    "QuestionDataChange")
                            .param(DataSummarizationRequest.VALUE_KEY,
                                    value.packString()));
                }
                dao.delete(answers);
            }
            SurveyInstance instance = dao.getByKey(instanceId);
            if (instance != null) {
                dao.delete(instance);
                log.log(Level.INFO, "Deleted: " + instanceId);
            }
        }
    }

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
