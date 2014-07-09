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

package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.dto.DeleteTaskRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.common.util.PropertyUtil;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class ScoreProcessor extends HttpServlet {

    public static final String ASYNC_TASK_TIMEOUT = "asyncTaskTimeout";
    /**
	 * 
	 */
    private static final long serialVersionUID = 5500271297082259592L;
    public static final String OBJECT_TASK_URL = "/app_worker/scoreprocessor";
    public static final String ACCESSPOINT_QUEUE_NAME = "accesspointscorequeue";
    private static final Logger log = Logger.getLogger(ScoreProcessor.class
            .getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.scorePoints(req);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        this.scorePoints(req);
    }

    static Integer executionCount = 0;

    private void scorePoints(HttpServletRequest req) {
        final Integer timeoutSeconds = Integer.parseInt(PropertyUtil
                .getProperty(ASYNC_TASK_TIMEOUT));
        executionCount++;
        log.info("Execution Count: " + executionCount);
        int scored_count = 0;
        final Integer FETCH_NUM_RECORDS = 10;
        boolean is_finished = false;

        AccessPointHelper aph = new AccessPointHelper();
        AccessPointDao apDao = new AccessPointDao();
        final DatastoreService dss = DatastoreServiceFactory
                .getDatastoreService();
        final long start = System.currentTimeMillis();
        PreparedQuery pquery;
        Cursor endCursor = null;
        while (System.currentTimeMillis() - start < timeoutSeconds) {

            final Query query = new Query("AccessPoint");

            query.setKeysOnly();

            final ArrayList<Key> keys = new ArrayList<Key>();
            String cursor = req.getParameter("cursor");

            pquery = dss.prepare(query);
            cursor = cursor.trim();
            FetchOptions fetchOptions = null;
            if (cursor != null && !cursor.equalsIgnoreCase("null") && !cursor.equals("")) {
                Cursor c = Cursor.fromWebSafeString(cursor);
                fetchOptions = FetchOptions.Builder.withLimit(FETCH_NUM_RECORDS).startCursor(c);
            } else {
                fetchOptions = FetchOptions.Builder.withLimit(FETCH_NUM_RECORDS);
            }
            QueryResultIterator<Entity> list = pquery.asQueryResultIterator(fetchOptions);
            while (list.hasNext()) {
                keys.add(list.next().getKey());
            }

            endCursor = list.getCursor();
            keys.trimToSize();

            if (keys.size() == 0) {
                is_finished = true;
                break;
            }

            while (System.currentTimeMillis() - start < timeoutSeconds) {

                try {
                    final Key apKey = keys.get(scored_count);
                    final AccessPoint ap = apDao.getByKey(apKey);
                    aph.scoreAccessPointNew(ap);
                    log.info("Scored :  " + ap.getKeyString());
                    scored_count++;
                    if (keys.size() - 1 == scored_count++) {
                        break;
                    }
                } catch (Throwable ignore) {
                    continue;
                }
            }
        }

        if (is_finished) {
            System.err.println("*** score job for AccessPoint is completed.");
        } else {

            final Integer taskcount;
            String taskCount = req.getParameter("taskCount");
            final String tcs = taskCount;
            if (tcs == null) {
                taskcount = 0;
            } else {
                taskcount = Integer.parseInt(tcs) + 1;
            }
            Queue deleteQueue = QueueFactory.getQueue(ACCESSPOINT_QUEUE_NAME);
            deleteQueue.add(TaskOptions.Builder.withUrl(OBJECT_TASK_URL).param(
                    DeleteTaskRequest.TASK_COUNT_PARAM, taskcount.toString())
                    .param("cursor", endCursor.toWebSafeString()));
            System.err.println("*** finished scoring " + scored_count + " APs");
            System.err.println("*** score task # " + taskcount + " for "
                    + "AccessPoint" + " is queued.");
        }
    }
}
