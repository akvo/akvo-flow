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
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExecuteRequestAsTaskFilterTests {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalTaskQueueTestConfig());

    private MockHttpServletRequest httpRequest;

    private String testPrivateKey = "very private";

    @BeforeEach
    public void setUp() {
        this.httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI("/testservletapi");
        httpRequest.setMethod("POST");
        httpRequest.setContentType("application/x-www-form-urlencoded");
        httpRequest.addParameter("action", "saveSurveyInstance");
        httpRequest.addParameter("collectionDate", "30-03-2017+10%3A05%3A57+CEST");
        httpRequest.addParameter("duration", "41");
        httpRequest.addParameter("questionId", "149382277%7C0%3D44.3845%257C7.5845%257C1%7Ctype%3DGEO");
        httpRequest.addParameter("questionId", "151492308%7C0%3Dhttp%253A%252F%252Fwaterforpeople.s3.amazonaws.com%252Fimages%252Fdb0756e0-b2ec-49ed-bbdb-26d4478af5c3.jpg%7Ctype%3DIMAGE");
        httpRequest.addParameter("questionId", "152332013%7C0%3Dfiunsc%7Ctype%3DVALUE");
        httpRequest.addParameter("questionId", "149292013%7C0%3D3%7Ctype%3DVALUE");
        httpRequest.addParameter("runAsTask", "1");
        httpRequest.addParameter("submitter", "tony");
        httpRequest.addParameter("surveyId", "15316201398");
        httpRequest.addParameter("surveyInstanceId", "15337202187");
        httpRequest.addParameter("ts", "2020/03/03 15:05:14");
        httpRequest.addParameter("h", "CmuehPsWW6//5Q4i8O1P5AjS/8Y=");

        helper.setUp();
    }

    @Test
    void testRequestToTaskMapping() {
        ExecuteRequestAsTaskFilter filter = new ExecuteRequestAsTaskFilter();
        ExecuteRequestAsTaskFilter.RequestToTaskMapper requestToTaskMapper = filter.new RequestToTaskMapper(this.httpRequest);

        Map<String, List<String>> taskRequestParams = requestToTaskMapper.getTaskOptions().getStringParams();
        assertEquals(10, taskRequestParams.size(), "Unexpected number of params for the request");
        assertEquals("/testservletapi", requestToTaskMapper.getTaskOptions().getUrl());
        assertEquals("saveSurveyInstance", taskRequestParams.get("action").get(0));
        assertEquals("2020/03/03 15:05:14", taskRequestParams.get("ts").get(0));
        assertEquals("30-03-2017+10%3A05%3A57+CEST", taskRequestParams.get("collectionDate").get(0));
        assertEquals("41", taskRequestParams.get("duration").get(0));
        assertEquals("CmuehPsWW6//5Q4i8O1P5AjS/8Y=", taskRequestParams.get("h").get(0));

        Map<String, List<String>> headers = requestToTaskMapper.getTaskOptions().getHeaders();
        assertTrue(headers.containsKey(RestRequest.QUEUED_TASK_HEADER), "The task queued header was not added");
    }

    @Test
    void testAddRequestToTaskQueue() throws IOException, ServletException {

        ExecuteRequestAsTaskFilter filter = new ExecuteRequestAsTaskFilter();
        filter.doFilter(this.httpRequest, new MockHttpServletResponse(), new MockFilterChain());

        assertTrue(filter.isTaskRequest(this.httpRequest), "The request MUST have a runAsTask parameter whose value is == 1");
        assertFalse(filter.isQueuedTask(this.httpRequest), "The request should not have an X-Akvo-Queued-Task header set");

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());

        Assertions.assertEquals(1, queueStateInfo.getTaskInfo().size());
    }

    @Test
    void testExecuteRequestIfQueued() throws IOException, ServletException {

        // comes from queued task
        this.httpRequest.addHeader(RestRequest.QUEUED_TASK_HEADER, "1");

        ExecuteRequestAsTaskFilter filter = new ExecuteRequestAsTaskFilter();
        filter.doFilter(this.httpRequest, new MockHttpServletResponse(), new MockFilterChain());

        assertTrue(filter.isTaskRequest(this.httpRequest), "The request MUST have a runAsTask parameter whose value is == 1");
        assertTrue(filter.isQueuedTask(this.httpRequest), "The request MUST have an X-Akvo-Queued-Task header set");

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());

        Assertions.assertEquals(0, queueStateInfo.getTaskInfo().size());
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }
}
