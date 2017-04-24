/*
 *  Copyright (C) 2015-2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.response.FormInstance;
import org.waterforpeople.mapping.domain.response.Response;

import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;

public class SurveyInstanceHandler {
    private static final Logger log = Logger.getLogger(SurveyInstanceHandler.class.getName());

    /**
     * TSV token indexes. For historical/legacy reasons, many indexes are empty or skipped.
     */
    private static final int SURVEY_ID = 0;
    private static final int QUESTION_ID = 2;
    private static final int ANSWER_TYPE = 3;
    private static final int ANSWER_VALUE = 4;
    private static final int USERNAME = 5;
    private static final int COLLECTION_DATE = 7;
    private static final int DEVICE_ID = 8;
    private static final int UUID = 11;
    private static final int DURATION = 12;
    private static final int DATAPOINT_ID = 13;

    public static SurveyInstance fromJSON(String data) {
        FormInstance formInstance = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formInstance = mapper.readValue(data, FormInstance.class);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error mapping JSON data: " + e.getMessage(), e);
            return null;
        }

        SurveyInstance si = new SurveyInstance();

        si.setUserID(1L);
        si.setCollectionDate(new Date(formInstance.getSubmissionDate()));
        si.setSubmitterName(formInstance.getUsername());
        si.setDeviceIdentifier(formInstance.getDeviceId());
        si.setSurveyalTime(formInstance.getDuration());
        si.setSurveyId(retrieveFormId(formInstance));
        si.setSurveyedLocaleIdentifier(formInstance.getDataPointId());
        si.setUuid(formInstance.getUUID());
        si.setQuestionAnswersStore(new ArrayList<QuestionAnswerStore>());

        // Process form responses
        for (Response response : formInstance.getResponses()) {
            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyId(si.getSurveyId());
            qas.setQuestionID(response.getQuestionId());
            qas.setCollectionDate(si.getCollectionDate());
            qas.setType(response.getAnswerType());
            qas.setValue(response.getValue());
            qas.setIteration(response.getIteration());

            // If one of the answer types is META_GEO or META_NAME, set up
            // the surveyedLocale corresponding attribute, and skip QAS
            if ("META_NAME".equals(qas.getType())) {
                si.setSurveyedLocaleDisplayName(qas.getValue());
            } else if ("META_GEO".equals(qas.getType())) {
                si.setLocaleGeoLocation(qas.getValue());
            } else {
                si.getQuestionAnswersStore().add(qas);
            }

        }

        return si;
    }

    /*
     * Retrieve the formId for form data coming in via data.json files. This extra check for the
     * formId has been created in order to handle a bug where the formId was returned as an
     * arbitrary string. We ensure that the formId is strictly a long otherwise we try to retrieve
     * it through the questionId values that come with each individual question response. See
     * https://github.com/akvo/akvo-flow-mobile/issues/614
     */
    private static Long retrieveFormId(FormInstance formInstance) {
        if (StringUtils.isNotBlank(formInstance.getFormId())
                && StringUtils.isNumeric(formInstance.getFormId())) {
            return Long.parseLong(formInstance.getFormId());
        }

        for (Response response : formInstance.getResponses()) {
            Long formId = retrieveFormIdByQuestionId(response.getQuestionId());
            if (formId != null) {
                return formId;
            }
        }

        return null;
    }

    /*
     * Retrieve the formId from the corresponding Question entity in the datastore
     */
    private static Long retrieveFormIdByQuestionId(String questionId) {
        if (questionId == null || questionId.trim().isEmpty()) {
            return null;
        }

        Question question = new QuestionDao().getByKey(Long.parseLong(questionId));

        if (question == null) {
            return null;
        }
        return question.getSurveyId();
    }

    public static SurveyInstance fromTSV(List<String> data) {
        final SurveyInstance si = new SurveyInstance();
        si.setUserID(1L);
        si.setQuestionAnswersStore(new ArrayList<QuestionAnswerStore>());

        boolean first = true;
        for (String line : data) {
            final String[] parts = line.split("\t");
            if (parts.length < UUID + 1) {
                return null;
            }

            if (first) {
                try {
                    si.setSurveyId(retrieveFormId(parts));
                    si.setCollectionDate(new Date(new Long(parts[COLLECTION_DATE].trim())));
                } catch (NumberFormatException e) {
                    log.log(Level.SEVERE, "Could not parse line: " + line, e);
                    return null;
                }
                si.setSubmitterName(parts[USERNAME].trim());
                si.setDeviceIdentifier(parts[DEVICE_ID].trim());
                si.setUuid(parts[UUID].trim());

                // Time and LocaleID. Old app versions might not include these columns.
                if (parts.length > DURATION) {
                    try {
                        si.setSurveyalTime(Long.valueOf(parts[DURATION].trim()));
                    } catch (NumberFormatException e) {
                        log.log(Level.WARNING, "Surveyal time column is not a number", e);
                    }
                }
                if (parts.length > DATAPOINT_ID) {
                    si.setSurveyedLocaleIdentifier(parts[DATAPOINT_ID].trim());
                }

                first = false;
            }

            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyId(si.getSurveyId());
            qas.setQuestionID(parts[QUESTION_ID].trim());
            qas.setType(parts[ANSWER_TYPE].trim());
            qas.setCollectionDate(si.getCollectionDate());
            qas.setValue(parts[ANSWER_VALUE].trim());

            // If one of the answer types is META_GEO or META_NAME, set up
            // the surveyedLocale corresponding attribute, and skip QAS
            if ("META_NAME".equals(qas.getType())) {
                si.setSurveyedLocaleDisplayName(qas.getValue());
            } else if ("META_GEO".equals(qas.getType())) {
                si.setLocaleGeoLocation(qas.getValue());
            } else {
                si.getQuestionAnswersStore().add(qas);
            }
        }

        return si;
    }

    /*
     * Parse formId for form data coming in via data.txt files. This extra check for the formId has
     * been created in order to handle a bug where the formId was returned as an arbitrary string.
     * We ensure that the formId is strictly a long otherwise we try to retrieve it through the
     * questionId values that come with each individual question response. See
     * https://github.com/akvo/akvo-flow-mobile/issues/614
     */
    private static Long retrieveFormId(String[] parts) {
        if (StringUtils.isNumeric(parts[SURVEY_ID].trim())) {
            return Long.parseLong(parts[SURVEY_ID].trim());
        } else {
            return retrieveFormIdByQuestionId(parts[QUESTION_ID].trim());
        }
    }
}
