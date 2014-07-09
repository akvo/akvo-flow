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

package com.gallatinsystems.framework.analytics.summarization;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Performs data summarization based on a statically defined list of summarizers (configured via the
 * init params)
 * 
 * @author Christopher Fagiani
 */
public abstract class DataSummarizationHandler extends AbstractRestApiServlet {
    private static final long serialVersionUID = 1689300636699166004L;
    protected Map<String, List<String>> summarizers;
    protected String queueName;
    protected String summarizerPath;

    public DataSummarizationHandler() {
        super();
        initializeSummarization();
    }

    /**
     * initializes the list of summarizers and the queue names/paths
     */
    protected abstract void initializeSummarization();

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        DataSummarizationRequest restRequest = new DataSummarizationRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    /**
     * looks up the appropriate summarizer in the summarizer map (using the Type passed in on the
     * request) then finds the name of the summarization and, if found, runs it. After running, if
     * there are more summarizations left in the list, it updates the action on the request with the
     * next value and puts the request back on the queue.
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected RestResponse handleRequest(RestRequest request) throws Exception {
        RestResponse response = new RestResponse();
        DataSummarizationRequest summarizationRequest = (DataSummarizationRequest) request;

        int idx = 0;
        List<String> applicableSummarizers = summarizers
                .get(summarizationRequest.getType());
        if (applicableSummarizers != null) {
            if (request.getAction() != null) {
                while (idx < applicableSummarizers.size()) {
                    if (applicableSummarizers.get(idx).trim().equals(
                            summarizationRequest.getAction())) {
                        break;
                    }
                    idx++;
                }
            }
            if (idx < applicableSummarizers.size()) {
                boolean isCompleted = false;
                DataSummarizer summarizer = null;
                try {
                    Class cls = Class.forName(applicableSummarizers.get(idx));
                    summarizer = (DataSummarizer) cls
                            .newInstance();
                    isCompleted = summarizer.performSummarization(
                            summarizationRequest.getObjectKey(),
                            summarizationRequest.getType(),
                            summarizationRequest.getValue(),
                            summarizationRequest.getOffset(),
                            summarizationRequest.getCursor());
                } catch (Exception e) {
                    log(
                            "Could not invoke summarizer. Setting complete to true to continue with processing",
                            e);
                    isCompleted = true;
                }
                if (!isCompleted && summarizer != null) {
                    // if we're not done, increment offset and call the same
                    // summarizer
                    summarizationRequest.setAction(applicableSummarizers
                            .get(idx));
                    summarizationRequest.setOffset(summarizationRequest
                            .getOffset()
                            + DataSummarizer.BATCH_SIZE);
                    summarizationRequest.setCursor(summarizer.getCursor());
                    invokeSummarizer(summarizationRequest);
                } else {
                    if (idx < applicableSummarizers.size() - 1) {
                        summarizationRequest.setAction(applicableSummarizers
                                .get(idx + 1));
                        // put the item back on the queue with the action
                        // updated to the next summarization in the chain
                        invokeSummarizer(summarizationRequest);
                    }
                }
            }
        } else {
            log("No summarizers configured for type "
                    + summarizationRequest.getType());
        }
        return response;
    }

    /**
     * puts the summarization request into the queue
     * 
     * @param request
     */
    private void invokeSummarizer(DataSummarizationRequest request) {
        Queue queue = QueueFactory.getQueue(queueName);
        queue
                .add(TaskOptions.Builder.withUrl(summarizerPath).param(
                        DataSummarizationRequest.ACTION_PARAM,
                        request.getAction()).param(
                        DataSummarizationRequest.OBJECT_KEY,
                        request.getObjectKey())
                        .param(DataSummarizationRequest.OBJECT_TYPE,
                                request.getType()).param(
                                DataSummarizationRequest.OFFSET_KEY,
                                request.getOffset().toString()).param(
                                DataSummarizationRequest.CURSOR_PARAM,
                                request.getCursor() != null ? request
                                        .getCursor() : ""));
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        resp.setCode("200");
    }
}
