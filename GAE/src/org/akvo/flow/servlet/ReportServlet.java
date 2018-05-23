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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.dao.ReportDao;
import org.akvo.flow.domain.persistent.Report;
import org.akvo.flow.rest.dto.ReportTaskRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.jfree.util.Log;
import org.waterforpeople.mapping.app.web.dto.TaskRequest;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.user.dao.UserDao;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

class ReportOptions {
    String exportMode;
    Long reportId;
    Long questionId; //only for GeoJSON
    Date from;
    Date to;
    Boolean lastCollection;
    String imgPrefix;
    String uploadUrl;
}

class ReportCriteria {
    ReportOptions opts;
    String exportType;
    String appId;
    Long surveyId;
    String email;
}

class ReportBody {
    ReportCriteria criteria;
}

public class ReportServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger.getLogger(ReportServlet.class.getName());

    private static final long serialVersionUID = -9064136799930675167L;

    private ReportDao rDao;
    private UserDao uDao;

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
        log.info("action: " + action + " id: " + id);
        if (action == null) {
            return null;
        }
        switch (action) {
            case ReportTaskRequest.START_ACTION:
                Report r = rDao.getByKey(id);
                if (r != null) {
                    if (!r.getState().equals(Report.QUEUED)) {
                        //wrong state
                        log.warning("Cannot start report that is " + r.getState());
                        //TODO do anything else?
                        return null;
                    }
                    //look up user
                    final String email = uDao.getByKey(r.getUser()).getEmailAddress();

                    //Gleaned from export-reports-views.js
                    ReportBody body = new ReportBody();
                    body.criteria = new ReportCriteria();
                    body.criteria.opts = new ReportOptions();
                    body.criteria.appId = PropertyUtil.getProperty("appId");
                    body.criteria.email = email;
                    body.criteria.surveyId = r.getFormId();
                    body.criteria.appId = PropertyUtil.getProperty("appId");
                    body.criteria.exportType = r.getReportType();
                    body.criteria.opts.exportMode = r.getReportType();
                    body.criteria.opts.reportId = r.getKey().getId();
                    body.criteria.opts.from = r.getStartDate();
                    body.criteria.opts.to = r.getEndDate();
                    body.criteria.opts.lastCollection = r.getLastCollectionOnly();
                    body.criteria.opts.questionId = r.getQuestionId();
                    body.criteria.opts.imgPrefix = PropertyUtil.getProperty("photo_url_root");
                    body.criteria.opts.uploadUrl = PropertyUtil.getProperty("surveyuploadurl");
                    ObjectMapper objectMapper = new ObjectMapper();
                    byte[] postData = objectMapper.writeValueAsBytes(body);//UTF-8?


                    //hit the services server
                    try {
                        URL url = new URL(PropertyUtil.getProperty("flowServices") + "/generate");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setRequestProperty("charset", "utf-8");
                        log.info("Preparing to POST " + body.toString() + " to  " + url);
                        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
                           wr.write( postData );
                        }
                        final int sts = con.getResponseCode();
                        log.info(" got  " + sts);

                        if (sts == 200) {
                            //Success, we are done!
                            return null;
                        }
                        //if permanent error, fail this report
                        else if ((sts % 100) == 4) { //4xx: you messed up
                            r.setState(Report.FINISHED_ERROR);
                            r.setMessage("Unexpected result when starting report: " + sts);
                            rDao.save(r);
                        }
                        else {
                            //if we get a transient error, re-queue
                            requeueStart(r);
                        }
                    }
                    catch (MalformedURLException e) {
                        Log.error("Bad URL");
                    }
                    catch (IOException e) {
                        //call it a transient error, re-queue
                        requeueStart(r);
                    }
                }
                break;
            default:
                log.warning("Unknown action.");
                break;
        }
        return null;
    }

    private void requeueStart(Report r) {
        //TODO check if this has been going on too long

        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = TaskOptions.Builder.withUrl("/app_worker/reportservlet")
                .param(TaskRequest.ACTION_PARAM, ReportTaskRequest.START_ACTION)
                .param(ReportTaskRequest.ID_PARAM, Long.toString(r.getKey().getId()));
        queue.add(options); //overwrite any supplied state
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // TODO Auto-generated method stub

    }

}
