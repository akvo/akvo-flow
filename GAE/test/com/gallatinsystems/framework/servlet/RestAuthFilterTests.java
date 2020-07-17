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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        mockHttpRequest.addParameter("questionId", "152332013%7C0%3Dfiunsc%7Ctype%3DVALUE");
        mockHttpRequest.addParameter("questionId", "149292013%7C0%3D3%7Ctype%3DVALUE");
        mockHttpRequest.addParameter("runAsTask", "1");
        mockHttpRequest.addParameter("submitter", "tony");
        mockHttpRequest.addParameter("surveyId", "15316201398");
        mockHttpRequest.addParameter("surveyInstanceId", "15337202187");
        mockHttpRequest.addParameter("ts", "2020/03/03 15:05:14");
        mockHttpRequest.addParameter("h", "CmuehPsWW6//5Q4i8O1P5AjS/8Y=");

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
    void testAuthorizationSucess() throws ServletException, IOException {
        mockFilterConfig.addInitParameter("enableRestSecurity", "true");

        RestAuthFilter restAuthFilter = new RestAuthFilter();
        restAuthFilter.init(mockFilterConfig);
        restAuthFilter.doFilter(mockHttpRequest, mockHttpResponse, new MockFilterChain());

        assertNull(mockHttpResponse.getErrorMessage());
    }

}
