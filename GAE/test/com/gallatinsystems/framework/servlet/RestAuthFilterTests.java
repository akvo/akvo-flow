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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class RestAuthFilterTests {

    private MockHttpServletRequest mockHttpRequest;

    private MockHttpServletResponse mockHttpResponse;

    private MockFilterConfig mockFilterConfig;

    @BeforeEach
    public void setUp() throws ServletException {

        this.mockHttpRequest = new MockHttpServletRequest();
        mockHttpRequest.setRequestURI("/datapoints");
        mockHttpRequest.setMethod("GET");
        mockHttpRequest.addParameter("action", "saveSurveyInstance");
        mockHttpRequest.addParameter("collectionDate", "30-03-2017+10%3A05%3A57+CEST");
        mockHttpRequest.addParameter("duration", "41");
        mockHttpRequest.addParameter("questionId", "149382277%7C0%3D44.3845%257C7.5845%257C1%7Ctype%3DGEO");
        mockHttpRequest.addParameter("questionId", "151492308%7C0%3Dhttp%253A%252F%252Fwaterforpeople.s3.amazonaws.com%252Fimages%252Fdb0756e0-b2ec-49ed-bbdb-26d4478af5c3.jpg%7Ctype%3DIMAGE");
        mockHttpRequest.addParameter("questionId", "151542031%7C0%3D%255B%257B%2522name%2522%253A%2522summer%2522%257D%252C%257B%2522name%2522%253A%2522autumn%2522%257D%252C%257B%2522name%2522%253A%2522winter%2522%257D%252C%257B%2522name%2522%253A%2522spring%2522%257D%255D%7Ctype%3DCASCADE");
        mockHttpRequest.addParameter("questionId", "152332013%7C0%3Dfiunsc%7Ctype%3DVALUE");
        mockHttpRequest.addParameter("questionId", "150452013%7C0%3D2786%7Ctype%3DVALUE");
        mockHttpRequest.addParameter("questionId", "149292013%7C0%3D3%7Ctype%3DVALUE");
        mockHttpRequest.addParameter("questionId", "144742013%7C0%3Dckbkchzhx%7Ctype%3DVALUE");
        mockHttpRequest.addParameter("runAsTask", "1");
        mockHttpRequest.addParameter("submitter", "jana");
        mockHttpRequest.addParameter("surveyId", "153162013");
        mockHttpRequest.addParameter("surveyInstanceId", "153372021");
        mockHttpRequest.addParameter("ts", "2020/07/17 16:03:36");
        mockHttpRequest.addParameter("h", "R82KP6xWR6rpJ8Jx6w9CjTXiKE0=");

        mockHttpResponse = new MockHttpServletResponse();

        mockFilterConfig = new MockFilterConfig();
        mockFilterConfig.addInitParameter("restPrivateKey", "very private");
    }

    @Test
    void testDisableRestSecurity() throws ServletException, IOException {
        mockFilterConfig.addInitParameter("enableRestSecurity", "false");

        // even with extra param that is not included in the hash test should pass
        mockHttpRequest.addParameter("extraParameter", "NotInHash");

        RestAuthFilter restAuthFilter = new RestAuthFilter();
        restAuthFilter.init(mockFilterConfig);
        restAuthFilter.doFilter(mockHttpRequest, mockHttpResponse, new MockFilterChain());

        assertNull(mockHttpResponse.getErrorMessage());
    }

    @Test
    void testAuthorizationFailureInvalidTimeStamp() throws ServletException, IOException {
        mockFilterConfig.addInitParameter("enableRestSecurity", "true");

        RestAuthFilter restAuthFilter = new RestAuthFilter();
        restAuthFilter.init(mockFilterConfig);
        restAuthFilter.doFilter(mockHttpRequest, mockHttpResponse, new MockFilterChain());

        assertEquals("Authorization failed", mockHttpResponse.getErrorMessage());
    }

    @Test
    void testValidHashGenerated() throws ServletException, IOException {
        mockFilterConfig.addInitParameter("enableRestSecurity", "true");

        RestAuthFilter restAuthFilter = new RestAuthFilter();
        restAuthFilter.init(mockFilterConfig);

        assertTrue(restAuthFilter.validateHashParam(mockHttpRequest));
    }

    @Test
    void testValidTimeStamp() throws ServletException, IOException {
        mockFilterConfig.addInitParameter("enableRestSecurity", "true");

        RestAuthFilter restAuthFilter = new RestAuthFilter();
        restAuthFilter.init(mockFilterConfig);

        // should fail because of invalid timestamp
        assertFalse(restAuthFilter.validateTimeStamp(mockHttpRequest));

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String incomingTimeStamp = df.format(new Date());
        mockHttpRequest.setParameter("ts", incomingTimeStamp);

        assertTrue(restAuthFilter.validateTimeStamp(mockHttpRequest));
    }

    @Test
    void testHashGenerationWithCursor() throws ServletException, IOException {
        mockFilterConfig.addInitParameter("enableRestSecurity", "true");

        mockHttpRequest.addParameter("cursor", "ClEKHwoSbGFzdFVwZGF0ZURhdGVUaW1lEgkI-PSu5tTU6gISKmoPYWt2b2Zsb3dzYW5kYm94chcLEg5TdXJ2ZXllZExvY2FsZRiDoK1GDBgAIAA");
        mockHttpRequest.setParameter("h", "v9ft3ERJ+qHI+9Str1HTwCvLRXs=");
        RestAuthFilter restAuthFilter = new RestAuthFilter();
        restAuthFilter.init(mockFilterConfig);

        assertTrue(restAuthFilter.validateHashParam(mockHttpRequest));
    }
}
