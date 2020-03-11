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

    public static final String REST_PRIVATE_KEY_PROP = "restPrivateKey";

    private String privateKey;

    private static final String RUN_AS_TASK = "1";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig.getInitParameter(REST_PRIVATE_KEY_PROP) != null) {
            privateKey = filterConfig.getInitParameter(REST_PRIVATE_KEY_PROP);
        } else {
            privateKey = PropertyUtil.getProperty(REST_PRIVATE_KEY_PROP);
        }
    }

    /*
     * Intercept a request that should be run as a task e.g. http://.../endpoint?runAsTask=1
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (isTaskRequest(request)) {
            executeRequestAsTask(
                    new RequestToTaskMapper(servletRequest, privateKey));
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

        final TaskOptions options = withDefaults();
        options.url(requestToTaskMapper.getUrl())
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

        for (Map.Entry<String, String[]> entry : requestToTaskMapper.getTaskRequestParams().entrySet()) {
            String key = entry.getKey();
            String[] paramValues = entry.getValue();

            for (int i = 0; i < paramValues.length; i++) {
                options.param(key, paramValues[i]);
            }
        }

        log.log(Level.FINE, "Query params: " + options.getStringParams());

        final Queue defaultQueue = QueueFactory.getDefaultQueue();
        defaultQueue.add(options);
    }

    /*
     * Class that takes the request parameters and converts them to
     * TaskOptions to be executed
     */
    class RequestToTaskMapper {

        private HttpServletRequest request;

        private String privateHashKey;

        private SortedMap<String, String[]> taskRequestParams;

        public RequestToTaskMapper(ServletRequest servletRequest, String privateKey) {
            this.privateHashKey = privateKey;
            this.request = (HttpServletRequest) servletRequest;
            this.taskRequestParams = generateTaskParameters(this.request.getParameterMap());
        }

        private SortedMap<String, String[]> generateTaskParameters(Map<String, String[]> parameterMap) {
            SortedMap<String, String[]> taskParams = new TreeMap<>();
            for (String paramKey : parameterMap.keySet()) {
                if (parameterShouldBeStripped(paramKey)) continue;
                taskParams.put(paramKey, parameterMap.get(paramKey));
            }
            taskParams.put(RestRequest.HASH_PARAM,
                    new String[] { generateParameterHash(taskParams) });
            return taskParams;
        }

        private String generateParameterHash(final Map<String, String[]> paramMap) {
            SortedMap<String, String[]> sortedTaskParams = new TreeMap(paramMap);

            final StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String[]> entry : sortedTaskParams.entrySet()) {
                String paramKey = entry.getKey();
                String[] values = entry.getValue();

                for (int i = 0; i < values.length; i++) {
                    try {
                        String encodedParam = URLEncoder.encode(values[i], "UTF-8");
                        queryString.append(paramKey).append("=").append(encodedParam).append("&");
                    } catch (UnsupportedEncodingException e) {
                        log.warning("Failed to encode parameter: " + paramKey + "=" + values[i]);
                    }
                }
            }
            queryString.deleteCharAt(queryString.lastIndexOf("&"));
            String hash = MD5Util.generateHMAC(queryString.toString(), privateHashKey);

            return hash;
        }

        private boolean parameterShouldBeStripped(String param) {
            return RestRequest.RUN_AS_TASK_PARAM.equalsIgnoreCase(param) ||
                    RestRequest.HASH_PARAM.equalsIgnoreCase(param);
        }

        public String getUrl() {
            return request.getRequestURI();
        }

        public SortedMap<String, String[]> getTaskRequestParams() {
            return taskRequestParams;
        }
    }
}
