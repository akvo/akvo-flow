/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.utils.SystemProperty;
import org.akvo.flow.dao.ReportDao;
import org.akvo.flow.domain.persistent.Report;
import org.akvo.flow.rest.dto.ReportTaskRequest;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.web.dto.TaskRequest;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.user.dao.UserDao;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


public class ReportServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(ReportServlet.class.getName());

    private static final long serialVersionUID = -9064136799930675167L;
    private static final String SERVLET_URL = "/app_worker/reportservlet";
    private static final Long MAX_ATTEMPTS = 5L; //Give up after this many attempts to start engine

    private ReportDao rDao;
    private UserDao uDao;

    class ReportOptions implements Serializable {
        /**
         * Used to send the start command to the report engine
         */
        private static final long serialVersionUID = 1L;
        public String exportMode;
        public Long reportId;
        public Long questionId; //only for GeoJSON
        public String from;
        public String to;
        public Boolean lastCollection;
        public String imgPrefix;
        public String uploadUrl;
        public String flowServices;
        public String appId;
        public String email;
    }

    class ReportCriteria implements Serializable {
        /**
         * Used to send the start command to the report engine
         */
        private static final long serialVersionUID = 1L;
        public ReportOptions opts;
        public String exportType;
        public String appId;
        public String surveyId;
        public String baseURL;
    }

    public ReportServlet() {
        rDao = new ReportDao();
        uDao = new UserDao();
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new ReportTaskRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception  {
        ReportTaskRequest stReq = (ReportTaskRequest) req;
        String action = stReq.getAction();
        Long id = stReq.getId();
        log.fine("action: " + action + " id: " + id + "attempt:" + stReq.getAttempt());
        Report r = rDao.getByKey(id);
        switch (action) {
            case ReportTaskRequest.START_ACTION:
                if (r != null) {
                    if (!r.getState().equals(Report.QUEUED)) {
                        //wrong state
                        log.warning("Cannot start report " + id + " that is " + r.getState());
                        //TODO do anything else?
                        return null;
                    }
                    log.fine(" ====Starting");

                    //hit the services server
                    int sts = 0;
                    try {
                        sts = startReportEngine(stReq.getBaseUrl(), r);
                        log.fine(" got  " + sts);

                        if (sts == 200) {
                            //Success, we are done!
                            return null;
                        } else if ((sts / 100) == 4) { //4xx: you messed up
                            //permanent error, fail this report
                            r.setState(Report.FINISHED_ERROR);
                            r.setMessage("Unexpected result when starting report " + id + " : " + sts);
                            rDao.save(r);
                        } else {
                            //if we get a transient error, re-queue
                            requeueStart(stReq, r, sts);
                        }
                    } catch (MalformedURLException e) {
                        log.log(Level.SEVERE, "Bad URL");
                    } catch (IOException e) {
                        log.warning("====IOerror: " + e);
                        //call it a transient error, re-queue
                        requeueStart(stReq, r, sts);
                    }
                }
                break;
            case ReportTaskRequest.PROGRESS_ACTION:
                if (r != null) {
                    if (!r.getState().equals(Report.QUEUED)
                            && !r.getState().equals(Report.IN_PROGRESS)) {
                        //wrong state
                        log.warning("Cannot set progress on report " + id + " that is " + r.getState());
                        return null;
                    }
                    r.setState(stReq.getState());
                    r.setMessage(stReq.getMessage());
                    r.setFilename(stReq.getFilename());
                    rDao.save(r);
                }
                break;
            default:
                log.warning("Unknown action.");
                break;
        }
        return null;
    }

    public static void queueStart(String baseUrl, Report r) {
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = getTaskOptions(baseUrl, 1L, r); //First time
        log.fine("Forking to task with options: " + options.toString());
        queue.add(options);
    }

    private static TaskOptions getTaskOptions(String baseUrl, Long attempt, Report r) {
        return TaskOptions.Builder.withUrl(SERVLET_URL)
                    .param(TaskRequest.ACTION_PARAM, ReportTaskRequest.START_ACTION)
                    .param(ReportTaskRequest.ID_PARAM, Long.toString(r.getKey().getId()))
                    .param(ReportTaskRequest.BASE_URL_PARAM, baseUrl)
                    .param(ReportTaskRequest.ATTEMPT_PARAM, Long.toString(attempt));
    }

    private void requeueStart(ReportTaskRequest req, Report r, int err) {
        //give up if this has been going on too long
        if (req.getAttempt() == null || req.getAttempt() >= MAX_ATTEMPTS) {
            log.warning("Abandoning START task after attempt " + req.getAttempt());
            r.setState(Report.FINISHED_ERROR);
            r.setMessage("Could not start report generation after " + MAX_ATTEMPTS + " attempts: " + err);
            rDao.save(r);
        } else {
            log.warning("Requeuing START task");

            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(getTaskOptions(req.getBaseUrl(), req.getAttempt() + 1, r));
        }
    }

    private int startReportEngine(String baseUrl, Report r)
            throws JsonGenerationException, JsonMappingException, IOException {
        //look up user
        final String email = uDao.getByKey(r.getUser()).getEmailAddress();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        		
        //Gleaned from export-reports-views.js
        ReportCriteria criteria = new ReportCriteria();
        criteria.opts = new ReportOptions();
        criteria.appId = SystemProperty.applicationId.get();
        criteria.surveyId = r.getFormId().toString();
        criteria.exportType = r.getReportType();
        criteria.baseURL = baseUrl;
        criteria.opts.appId = SystemProperty.applicationId.get();
        criteria.opts.exportMode = r.getReportType();
        criteria.opts.reportId = r.getKey().getId();
        if (r.getStartDate() != null) {
        	criteria.opts.from = sdf.format(r.getStartDate());
        }
        if (r.getEndDate() != null) {
        	criteria.opts.to = sdf.format(r.getEndDate());
        }
        criteria.opts.lastCollection = r.getLastCollectionOnly();
        criteria.opts.questionId = r.getQuestionId();
        criteria.opts.imgPrefix = PropertyUtil.getProperty("photo_url_root");
        criteria.opts.uploadUrl = PropertyUtil.getProperty("surveyuploadurl");
        criteria.opts.flowServices = PropertyUtil.getProperty("flowServices");
        criteria.opts.email = email;
        ObjectMapper objectMapper = new ObjectMapper();
        String crit = java.net.URLEncoder.encode(objectMapper.writeValueAsString(criteria), "UTF-8");

        URL url = new URL(PropertyUtil.getProperty("flowServices") + "/generate?criteria=" +  crit);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        log.log(Level.FINE, "Preparing to GET " + url);
        return con.getResponseCode();
    }


    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // TODO Auto-generated method stub

    }

}
