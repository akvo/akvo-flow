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

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.task.domain.Task;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.RetryOptions.Builder.withTaskRetryLimit;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withDefaults;

public class ExecuteRequestAsTaskFilter implements Filter {

    private static Logger log = Logger.getLogger(ExecuteRequestAsTaskFilter.class
            .getName());

    private static final String RUN_AS_TASK = "1";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*
     * Intercept a request that should be run as a task e.g. http://.../endpoint?runAsTask=1
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (isTaskRequest(request)) {
            executeRequestAsTask(new RequestToTaskMapper(request));
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public boolean isTaskRequest(HttpServletRequest request) {
        String runAsTask = request.getParameter(RestRequest.RUN_AS_TASK_PARAM);
        return RUN_AS_TASK.equals(runAsTask);
    }

    @Override
    public void destroy() {
    }

    private void executeRequestAsTask(final RequestToTaskMapper requestToTaskMapper) {
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
