/*
 *  Copyright (C) 2010-2012, 2018 Stichting Akvo (Akvo Foundation)
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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.device.dao.DeviceFileJobQueueDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionHelpMediaDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class SurveyTaskServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(SurveyTaskServlet.class
            .getName());

    private static final long serialVersionUID = -9064136783930675167L;

    private AccessPointHelper aph;
    private SurveyInstanceDAO siDao;

    public SurveyTaskServlet() {
        aph = new AccessPointHelper();
        siDao = new SurveyInstanceDAO();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new SurveyTaskRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        SurveyTaskRequest stReq = (SurveyTaskRequest) req;
        String action = stReq.getAction();
        Long id = stReq.getId();
        log.info("action: " + action + " id: " + id);
        if (action == null) {
            return null;
        }
        switch (action) {
            case SurveyTaskRequest.DELETE_SURVEY_ACTION:
                SurveyDAO surveyDao = new SurveyDAO();
                Survey s = surveyDao.getByKey(id);
                if(s != null) {
                    surveyDao.delete(s);
                }
                break;
            case SurveyTaskRequest.DELETE_QUESTION_GROUP_ACTION:
                QuestionGroupDao qgDao = new QuestionGroupDao();
                QuestionGroup qg = qgDao.getByKey(id);
                if(qg != null) {
                    qgDao.delete(qg);
                }
                break;
            case SurveyTaskRequest.DELETE_QUESTION_ACTION:
                QuestionDao qDao = new QuestionDao();
                Question q = qDao.getByKey(id);
                if( q != null) {
                    qDao.delete(q);
                }
                break;
            case SurveyTaskRequest.DELETE_QUESTION_OPTION_ACTION:
                QuestionOptionDao qoDao = new QuestionOptionDao();
                qoDao.delete(qoDao.getByKey(id));
                break;
            case SurveyTaskRequest.DELETE_QUESTION_HELP_ACTION:
                QuestionHelpMediaDao qhDao = new QuestionHelpMediaDao();
                qhDao.delete(qhDao.getByKey(id));
                break;
            case SurveyTaskRequest.DELETE_QUESTION_TRANSLATION_ACTION:
                TranslationDao tDao = new TranslationDao();
                tDao.delete(tDao.getByKey(id));
                break;
            case SurveyTaskRequest.DELETE_DSJQ_ACTION:
                DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
                dsjqDao.deleteJob(id);
                break;
            case SurveyTaskRequest.DELETE_DFJQ_ACTION:
                DeviceFileJobQueueDAO dfjqDao = new DeviceFileJobQueueDAO();
                dfjqDao.delete(dfjqDao.getByKey(id));
                break;
            case SurveyTaskRequest.REMAP_SURVEY_INSTANCE:
                String idList = stReq.getIdList();
                if (idList != null && idList.trim().length() > 0) {
                    String[] ids = idList.split(",");
                    log.log(Level.INFO, "action: " + stReq.getAction() + " idList: " + idList);
                    for (int i = 0; i < ids.length; i++) {
                        aph.processSurveyInstance(ids[i]);
                    }
                    if (stReq.getCursor() != null) {
                        List<SurveyInstance> nextSet = siDao
                                .listSurveyInstanceBySurveyId(stReq.getId(), stReq.getCursor());
                        if (nextSet != null && nextSet.size() > 0) {
                            StringBuffer buffer = new StringBuffer();
                            Queue queue = QueueFactory.getDefaultQueue();
                            for (int i = 0; i < nextSet.size(); i++) {
                                if (i > 0) {
                                    buffer.append(",");
                                }
                                buffer.append(nextSet.get(i).getKey().getId());
                            }
                            queue.add(TaskOptions.Builder.withUrl("/app_worker/surveytask")
                                    .param(SurveyTaskRequest.ACTION_PARAM,
                                            SurveyTaskRequest.REMAP_SURVEY_INSTANCE)
                                    .param(SurveyTaskRequest.ID_PARAM, stReq.getId().toString())
                                    .param(SurveyTaskRequest.ID_LIST_PARAM, buffer.toString())
                                    .param(SurveyTaskRequest.CURSOR_PARAM,
                                            SurveyInstanceDAO.getCursor(nextSet)));
                        }
                    }
                }
                break;
            default:log.warning("Unknown action.");
        }
        return null;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // TODO Auto-generated method stub

    }

}
