/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Dao for manipulating CascadeResources
 */
public class CascadeResourceDao extends BaseDAO<CascadeResource> {

    private static final Logger log = Logger.getLogger(CascadeResourceDao.class.getName());

    public CascadeResourceDao() {
		super(CascadeResource.class);
	}


    /**
     * deletes a cascade resource, and the nodes therein asynchronously in a task
     *
     * @param item
     */
    public void delete(CascadeResource item) {
        if (item == null) {
            return;
        }

        try {

            super.delete(item);

            final Long keyId = item.getKey().getId();
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .header("Host",
                            BackendServiceFactory.getBackendService()
                                    .getBackendAddress("dataprocessor"))
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.DELETE_CASCADE_NODES)
                    .param(DataProcessorRequest.CASCADE_RESOURCE_ID, keyId.toString());
            final Queue queue = QueueFactory.getQueue("background-processing");
            queue.add(options);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error deleting Cascade Resource: " + e.getMessage(), e);
        }
    }
}
