/*
 *  Copyright (C) 2020,2022 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.domain;

import com.gallatinsystems.common.util.PropertyUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.waterforpeople.mapping.app.web.EnvServlet.WEBFORM_V2_ENABLED;
import static org.waterforpeople.mapping.app.web.EnvServlet.WEBFORM_V2_URL_GENERATE_ENDPOINT;

public class WebForm {
    private static final Logger logger = Logger.getLogger(WebForm.class.getName());

    public static Set<String> unsupportedQuestionTypes() {
        Set<String> unsupportedTypes = new HashSet<String>();
        unsupportedTypes.add(Question.Type.GEOSHAPE.toString());
        unsupportedTypes.add(Question.Type.SIGNATURE.toString());
        unsupportedTypes.add(Question.Type.CADDISFLY.toString());
        return unsupportedTypes;
    }

    public static boolean validForm(final Survey survey, final SurveyGroup surveyGroup) {
        return !surveyGroup.getMonitoringGroup() ||
                (surveyGroup.getNewLocaleSurveyId() != null &&
                surveyGroup.getNewLocaleSurveyId().equals(survey.getKey().getId()));
    }

    public static boolean validWebForm(final SurveyGroup surveyGroup, final Survey survey, final List<Question> questions) {
        boolean validSurveyGroup = validForm(survey, surveyGroup);
        if (!validSurveyGroup) {
            return false;
        }
        List<Question> validQuestions = questions.stream().filter(i -> !unsupportedQuestionTypes().contains(i.getType().toString())).collect(Collectors.toList());
        return validQuestions.size() == questions.size();
    }

    public static String generateWebFormV2Uri(Long formId) {
        String baseUri = PropertyUtil.getProperty(WEBFORM_V2_URL_GENERATE_ENDPOINT);
        String alias = PropertyUtil.getProperty("alias").split("\\.")[0];
        String fullUri = String.format("%s/%s/%s", baseUri, alias, formId);

        logger.fine("Generating Uri using endpoint: " + fullUri);
        String response = null;
        try {
            response = executeHttpRequest(fullUri);
            if (response.length() > 1500) { // TODO: Verify the URI generation algorithm for max size
                throw new IOException("Response is too large");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while retrieving webformV2 form endpoint:" + e.getMessage(), e);
        }
        return response;
    }

    public static boolean isWebFormV2Enabled() {
        return "true".equalsIgnoreCase(PropertyUtil.getProperty(WEBFORM_V2_ENABLED));
    }

    private static String executeHttpRequest(String fullUri) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(fullUri);
            ResponseHandler<String> responseHandler = (response) -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    return null;
                }
            };
            return httpclient.execute(httpget, responseHandler);
        } finally {
            httpclient.close();
        }
    }
}
