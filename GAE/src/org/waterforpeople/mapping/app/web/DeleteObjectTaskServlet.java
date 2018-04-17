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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.DeleteTaskRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class DeleteObjectTaskServlet extends AbstractRestApiServlet {

    private static final String DELETE_OBJECT_TASK_URL = "/app_worker/deleteobjecttask";
    private static final String DELETE_QUEUE_NAME = "deletequeue";
    /**
	 * 
	 */
    private static final long serialVersionUID = -7978453807761868626L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DeleteTaskRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DeleteTaskRequest dtReq = (DeleteTaskRequest) convertRequest();
        if (dtReq.getKey().equals("secret")) {
            deleteObject(dtReq.getObjectName(), dtReq.getTaskCount(), dtReq.getApiKey());
        }

        return null;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        if (resp != null) {
            getResponse().getWriter().println("ok");
        }
    }

    private void deleteObject(String objectName, String taskCount, String key) {
        final String kind = objectName;

        int deleted_count = 0;
        boolean is_finished = false;

        final DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
        final long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 16384) {

            final Query query = new Query(kind);

            query.setKeysOnly();

            final ArrayList<Key> keys = new ArrayList<Key>();

            for (final Entity entity : dss.prepare(query).asIterable(
                    FetchOptions.Builder.withLimit(128))) {
                keys.add(entity.getKey());
            }

            keys.trimToSize();

            if (keys.size() == 0) {
                is_finished = true;
                break;
            }

            while (System.currentTimeMillis() - start < 16384) {

                try {
                    dss.delete(keys);
                    deleted_count += keys.size();
                    break;
                } catch (Throwable ignore) {
                    continue;
                }
            }
        }
        System.err.println("*** deleted " + deleted_count + " entities from "
                + kind);

        if (is_finished) {
            System.err.println("*** deletion job for " + kind
                    + " is completed.");
        } else {
            final Integer taskcount;
            final String tcs = taskCount;
            if (tcs == null) {
                taskcount = 0;
            } else {
                taskcount = Integer.parseInt(tcs) + 1;
            }

            Queue deleteQueue = QueueFactory.getQueue(DELETE_QUEUE_NAME);
            deleteQueue.add(TaskOptions.Builder.withUrl(DELETE_OBJECT_TASK_URL)
                    .param(DeleteTaskRequest.OBJECT_PARAM, kind + "")
                    .param(DeleteTaskRequest.KEY_PARAM, key)
                    .param(DeleteTaskRequest.TASK_COUNT_PARAM, taskcount.toString()));

            System.err.println("*** deletion task # " + taskcount + " for "
                    + kind + " is queued.");

        }
    }

}
