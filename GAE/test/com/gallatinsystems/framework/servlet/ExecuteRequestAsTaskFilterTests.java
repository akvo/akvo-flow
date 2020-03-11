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
import java.util.SortedMap;

import static com.gallatinsystems.framework.servlet.ExecuteRequestAsTaskFilter.REST_PRIVATE_KEY_PROP;
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
        httpRequest.addParameter("runAsTask", "1");
        httpRequest.addParameter("collectionDate", "30-03-2017+10%3A05%3A57+CEST");
        httpRequest.addParameter("duration", "41");
        httpRequest.addParameter("questionId", "149382277%7C0%3D44.3845%257C7.5845%257C1%7Ctype%3DGEO");
        httpRequest.addParameter("questionId", "151492308%7C0%3Dhttp%253A%252F%252Fwaterforpeople.s3.amazonaws.com%252Fimages%252Fdb0756e0-b2ec-49ed-bbdb-26d4478af5c3.jpg%7Ctype%3DIMAGE");
        httpRequest.addParameter("questionId", "152332013%7C0%3Dfiunsc%7Ctype%3DVALUE");
        httpRequest.addParameter("questionId", "149292013%7C0%3D3%7Ctype%3DVALUE");
        httpRequest.addParameter("submitter", "tony");
        httpRequest.addParameter("surveyId", "15316201398");
        httpRequest.addParameter("surveyInstanceId", "15337202187");
        httpRequest.addParameter("ts", "2020/03/03 15:05:14");

        helper.setUp();
    }

    @Test
    void testExecuteRequestTask() {
        ExecuteRequestAsTaskFilter filter = new ExecuteRequestAsTaskFilter();
        ExecuteRequestAsTaskFilter.RequestToTaskMapper requestToTaskMapper = filter.new RequestToTaskMapper(this.httpRequest, testPrivateKey);

        assertTrue(filter.isTaskRequest(this.httpRequest), "The *original* request MUST have a runAsTask parameter whose value is == 1");

        SortedMap<String, String[]> sortedTaskParams = requestToTaskMapper.getTaskRequestParams();
        assertEquals(9, sortedTaskParams.size(), "Unexpected number of params for the request");
        assertEquals("/testservletapi", requestToTaskMapper.getUrl());
        assertEquals("action", sortedTaskParams.firstKey());
        assertEquals("ts", sortedTaskParams.lastKey());
        assertEquals("30-03-2017+10%3A05%3A57+CEST", sortedTaskParams.get("collectionDate")[0]);
        assertEquals("41", sortedTaskParams.get("duration")[0]);
        assertEquals("149382277%7C0%3D44.3845%257C7.5845%257C1%7Ctype%3DGEO", sortedTaskParams.get("questionId")[0]);
        assertEquals("151492308%7C0%3Dhttp%253A%252F%252Fwaterforpeople.s3.amazonaws.com%252Fimages%252Fdb0756e0-b2ec-49ed-bbdb-26d4478af5c3.jpg%7Ctype%3DIMAGE", sortedTaskParams.get("questionId")[1]);
        assertEquals("152332013%7C0%3Dfiunsc%7Ctype%3DVALUE", sortedTaskParams.get("questionId")[2]);
        assertEquals("149292013%7C0%3D3%7Ctype%3DVALUE", sortedTaskParams.get("questionId")[3]);
        assertEquals("2020/03/03 15:05:14", sortedTaskParams.get(RestRequest.TIMESTAMP_PARAM)[0]);
        assertEquals("CmuehPsWW6//5Q4i8O1P5AjS/8Y=", sortedTaskParams.get(RestRequest.HASH_PARAM)[0]);
    }

    @Test
    void testExecuteRequestAsTaskFilter() throws IOException, ServletException {

        FilterConfig mockConfig = mock(FilterConfig.class);
        when(mockConfig.getInitParameter(REST_PRIVATE_KEY_PROP)).thenReturn(testPrivateKey);

        ExecuteRequestAsTaskFilter filter = new ExecuteRequestAsTaskFilter();
        filter.init(mockConfig);
        filter.doFilter(this.httpRequest, new MockHttpServletResponse(), new MockFilterChain());

        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        QueueStateInfo queueStateInfo = localTaskQueue.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());

        Assertions.assertEquals(1, queueStateInfo.getTaskInfo().size());
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }
}
