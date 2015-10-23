/*
 *  Copyright (C) 2014-2015 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.CascadeNode;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Dao for manipulating CascadeResources
 */
public class CascadeNodeDao extends BaseDAO<CascadeNode> {
    public CascadeNodeDao() {
        super(CascadeNode.class);
    }

    private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
            .getName());

    /*
     * List Cascade nodes by cascade resource id
     */
    public List<CascadeNode> listCascadeNodesByResource(Long cascadeResourceId) {
        List<CascadeNode> cnList = listByProperty("cascadeResourceId", cascadeResourceId,
                "Long", "name", "asc");
        return cnList;
    }

    /*
     * List Cascade nodes by cascade resource ID and parent node
     */
    @SuppressWarnings("unchecked")
    public List<CascadeNode> listCascadeNodesByResourceAndParentId(Long cascadeResourceId,
            Long parentNodeId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(CascadeNode.class);
        Map<String, Object> paramMap = null;
        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("cascadeResourceId", filterString, paramString, "Long",
                cascadeResourceId, paramMap);
        appendNonNullParam("parentNodeId", filterString, paramString, "Long",
                parentNodeId, paramMap);

        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        return (List<CascadeNode>) query.executeWithMap(paramMap);
    }

    public void deleteRecursive(Long cascadeResourceId, Long nodeId) {
        CascadeNode cr = getByKey(nodeId);
        if (cr == null) {
            return;
        }
        try {
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .header("Host",
                            BackendServiceFactory.getBackendService()
                                    .getBackendAddress("dataprocessor"))
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.DELETE_CASCADE_NODES)
                    .param(DataProcessorRequest.CASCADE_RESOURCE_ID,
                            cascadeResourceId.toString())
                    .param(DataProcessorRequest.PARENT_NODE_ID, nodeId.toString());
            final Queue queue = QueueFactory.getQueue("background-processing");
            queue.add(options);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    String.format(
                            "Error scheduling Cascade Node deletion - cascadeResourceId: %s - parentNodeId: %s",
                            cascadeResourceId, nodeId), e);
        }
        delete(cr);
    }

    public List<CascadeNode> listByName(Long cascadeResourceId, List<String> cascadeNodeNames) {
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "cascadeResourceId == :p1 && :p2.contains(name)";
        javax.jdo.Query query = pm.newQuery(CascadeNode.class, queryString);

        return (List<CascadeNode>) query.execute(cascadeResourceId, cascadeNodeNames);
    }
}
