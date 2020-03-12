/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.framework.servlet;

import com.gallatinsystems.framework.rest.RestRequest;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.RetryOptions.Builder.withTaskRetryLimit;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withDefaults;

public class ExecuteRequestAsTaskFilter implements Filter {

    private static Logger log = Logger.getLogger(ExecuteRequestAsTaskFilter.class
            .getName());

    private static final String RUN_AS_TASK_VALUE = "1";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*
     * Intercept a request that should be run as a task e.g. http://.../endpoint?runAsTask=1
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (isQueuedTask(request) || !isTaskRequest(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            addRequestToTaskQueue(new RequestToTaskMapper(request));
        }
    }

    public boolean isTaskRequest(HttpServletRequest request) {
        String runAsTaskParamValue = request.getParameter(RestRequest.RUN_AS_TASK_PARAM);
        return RUN_AS_TASK_VALUE.equals(runAsTaskParamValue);
    }

    public boolean isQueuedTask(HttpServletRequest request) {
        String isQueuedTaskHeaderValue = request.getHeader(RestRequest.QUEUED_TASK_HEADER);
        return isTaskRequest(request)
                && RUN_AS_TASK_VALUE.equals(isQueuedTaskHeaderValue);
    }

    @Override
    public void destroy() {
    }

    private void addRequestToTaskQueue(final RequestToTaskMapper requestToTaskMapper) {
        final Queue defaultQueue = QueueFactory.getDefaultQueue();
        defaultQueue.add(requestToTaskMapper.getTaskOptions());
    }

    /*
     * Class that takes the request parameters and converts them to
     * TaskOptions to be executed
     */
    class RequestToTaskMapper {

        private HttpServletRequest request;

        private TaskOptions taskOptions;

        public RequestToTaskMapper(HttpServletRequest req) {
            this.request = req;
            this.taskOptions = mapRequestToTaskOptions(this.request);
        }

        private TaskOptions mapRequestToTaskOptions(HttpServletRequest request) {
            final TaskOptions options = withDefaults();
            options.url(request.getRequestURI())
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header(RestRequest.QUEUED_TASK_HEADER, "1")
                    .method(TaskOptions.Method.POST)
                    .retryOptions(withTaskRetryLimit(5)
                            .minBackoffSeconds(5)
                            .maxBackoffSeconds(120) // requests become invalid after 10mins due to the
                            // ts parameter. So the total time to complete
                            // wait and reschedule should be under 10 mins
                            // from the first request
                            // see RestAuthFilter.isTimeStampValid()
                            .maxDoublings(5));

            Map paramMap = request.getParameterMap();
            for (Object k : paramMap.keySet()) {
                String key = (String) k;
                String[] paramValues = (String[]) paramMap.get(key);

                for (int i = 0; i < paramValues.length; i++) {
                    options.param(key, paramValues[i]);
                }
            }

            log.log(Level.FINE, "Query params: " + options.getStringParams());

            return options;
        }

        public TaskOptions getTaskOptions() {
            return taskOptions;
        }
    }
}
