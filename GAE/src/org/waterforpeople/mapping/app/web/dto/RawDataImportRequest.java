/*
 *  Copyright (C) 2010-2015, 2019, 2021 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.dto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

public class RawDataImportRequest extends RestRequest {
    private static final Logger log = Logger.getLogger("RawDataImportRequest");

    private static final long serialVersionUID = 3792808180110794885L;
    private static final ThreadLocal<DateFormat> IN_FMT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        };
    };

    public static final String SURVEY_INSTANCE_ID_PARAM = "surveyInstanceId";
    public static final String COLLECTION_DATE_PARAM = "collectionDate";
    public static final String QUESTION_ID_PARAM = "questionId";
    public static final String SURVEY_ID_PARAM = "surveyId";
    public static final String SUBMITTER_PARAM = "submitter";
    public static final String FIXED_FIELD_VALUE_PARAM = "values";
    public static final String LOCALE_ID_PARAM = "surveyedLocale";
    public static final String DURATION_PARAM = "duration";
    public static final String FORM_VER_PARAM = "formVersion";

    public static final String SAVE_SURVEY_INSTANCE_ACTION = "saveSurveyInstance";
    public static final String RESET_SURVEY_INSTANCE_ACTION = "resetSurveyInstance";
    public static final String SAVE_FIXED_FIELD_SURVEY_INSTANCE_ACTION = "ingestFixedFormat";
    public static final String UPDATE_SUMMARIES_ACTION = "updateSummaries";
    public static final String SAVE_MESSAGE_ACTION = "saveMessage";

    public static final String FIELD_VAL_DELIMITER = ";;";

    private Long surveyId;
    private Long surveyedLocaleId;
    private Long surveyInstanceId = null;
    private Long duration = null;
    private Date collectionDate = null;
    private String submitter = null;
    private Double formVersion = null;

    // questionId -> iteration -> [response, type]
    private Map<Long, Map<Integer, String[]>> responseMap = new HashMap<>();

    private List<String> fixedFieldValues;

    private SurveyInstanceDAO instanceDao;
    private SurveyDAO sDao;
    private SurveyGroupDAO sgDao;
    private SurveyedLocaleDao slDao;

    private SurveyedLocale dataPoint;

    private SurveyInstance formInstance;

    private Survey form;

    private SurveyGroup survey;

    public RawDataImportRequest() {
        instanceDao = new SurveyInstanceDAO();
        sDao = new SurveyDAO();
        sgDao = new SurveyGroupDAO();
        slDao = new SurveyedLocaleDao();
    }

    public Survey getForm() {
        return form;
    }

    public SurveyGroup getSurvey() {
        return survey;
    }

    public SurveyInstance getFormInstance() {
        return formInstance;
    }

    public SurveyedLocale getDataPoint() {
        return dataPoint;
    }

    public boolean isNewFormInstance() {
        return surveyInstanceId == null;
    }

    public boolean isMonitoringForm() {
        return survey != null &&
                form != null &&
                survey.getMonitoringGroup() &&
                !survey.getNewLocaleSurveyId().equals(form.getKey().getId());
    }

    public boolean isRegistrationForm() {
        return survey != null &&
                form != null &&
                survey.getMonitoringGroup() &&
                survey.getNewLocaleSurveyId().equals(form.getKey().getId());
    }

    public List<String> getFixedFieldValues() {
        return fixedFieldValues;
    }

    public void setFixedFieldValues(List<String> fixedFieldValues) {
        this.fixedFieldValues = fixedFieldValues;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Map<Long, Map<Integer, String[]>> getResponseMap() {
        return responseMap;
    }

    public void putResponse(Long questionId, Integer iteration, String value, String type) {

        Map<Integer, String[]> iterationMap = responseMap.get(questionId);

        if (iterationMap == null) {
            iterationMap = new HashMap<>();
            responseMap.put(questionId, iterationMap);
        }

        iterationMap.put(iteration, new String[] {
                value,
                (type != null ? type : "VALUE")
        });
    }

    @Override
    protected void populateErrors() {
        List<String> errors =  validateRequest();
        for (String error : errors) {
            this.addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE, error, surveyInstanceId.toString()));
        }
    }

    /*
     * Validate the incoming request parameters are what is required
     */
    public List<String> validateRequest() {
        List<String> validationErrors = new ArrayList<>();
        if (SAVE_SURVEY_INSTANCE_ACTION.equals(this.getAction())) {
            validationErrors.addAll(validateSaveSurveyInstanceRequest());
        }
        return validationErrors;
    }

    private List<String> validateSaveSurveyInstanceRequest() {
        final List<String> validationErrors = new ArrayList<>();

        if (surveyInstanceId != null) {
            this.formInstance = instanceDao.getByKey(surveyInstanceId);
            if (this.formInstance == null) {
                validationErrors.add("Form instance [id=" + surveyInstanceId + "] not found");
            }
        }

        if (surveyId != null) {
            form = sDao.getByKey(surveyId);
            if (form == null) {
                validationErrors.add("Form [id=" + surveyId + "] not found");
            }
        }

        if (form != null) {
            survey = sgDao.getByKey(form.getSurveyGroupId());
            if (survey == null) {
                validationErrors.add("Survey [id=" + form.getSurveyGroupId() + "] not found");
            }
        }

        if (this.isMonitoringForm()) {
            validationErrors.add("Importing new data into a monitoring form is not supported at the moment");
        }

        if (this.formInstance != null &&
                this.surveyId != null &&
                !this.formInstance.getSurveyId().equals(surveyId)) {
            validationErrors.add("Wrong survey selected when importing instance [id=" + surveyInstanceId + "]");
        }

        if (this.formInstance != null &&
                this.survey != null &&
                this.survey.getMonitoringGroup()) {
            if (this.formInstance.getSurveyedLocaleId() == null) {
                validationErrors.add("Form instance [id=" + surveyInstanceId + "] does not have an associated datapoint");
            } else {
                this.dataPoint = slDao.getByKey(this.formInstance.getSurveyedLocaleId());
                if (dataPoint == null) {
                    validationErrors.add("Associated datapoint is missing [ datapoint id = " + formInstance.getSurveyedLocaleId() + "]");
                }
            }
        }
        return validationErrors;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {

        if (req.getParameter(LOCALE_ID_PARAM) != null) {
            try {
                setSurveyedLocaleId(new Long(req.getParameter(LOCALE_ID_PARAM)));
            } catch (Exception e) {
                log.info(LOCALE_ID_PARAM + " is missing");
            }
        }
        if (req.getParameter(SURVEY_INSTANCE_ID_PARAM) != null) {
            try {
                setSurveyInstanceId(new Long(
                        req.getParameter(SURVEY_INSTANCE_ID_PARAM)));
            } catch (Exception e) {
                log.info(SURVEY_INSTANCE_ID_PARAM + " is missing");
            }
        }
        if (req.getParameter(FIXED_FIELD_VALUE_PARAM) != null) {
            fixedFieldValues = new ArrayList<String>();
            String[] vals = URLDecoder.decode(req.getParameter(FIXED_FIELD_VALUE_PARAM), "UTF-8")
                    .split(FIELD_VAL_DELIMITER);
            for (int i = 0; i < vals.length; i++) {
                fixedFieldValues.add(vals[i]);
            }
        }
        if (req.getParameter(QUESTION_ID_PARAM) != null) {
            handleQuestionIdParam(req.getParameterValues(QUESTION_ID_PARAM));
        } else {
            log.warning("No question answers to import");
        }
        if (req.getParameter(SURVEY_ID_PARAM) != null) {
            surveyId = new Long(req.getParameter(SURVEY_ID_PARAM).trim());
        } else {
            log.warning(SURVEY_ID_PARAM + " is missing");
        }
        if (req.getParameter(COLLECTION_DATE_PARAM) != null
                && req.getParameter(COLLECTION_DATE_PARAM).trim().length() > 0) {
            String colDate = req.getParameter(COLLECTION_DATE_PARAM).trim();
            if (colDate.contains("%") || colDate.contains("+")) {
                colDate = URLDecoder.decode(colDate, "UTF-8");
            }
            collectionDate = IN_FMT.get().parse(colDate);
        }
        if (req.getParameter(SUBMITTER_PARAM) != null) {
            setSubmitter(URLDecoder.decode(
                    req.getParameter(SUBMITTER_PARAM), "UTF-8"));
        }
        if (req.getParameter(DURATION_PARAM) != null) {
            try {
                setSurveyDuration(Long.valueOf(req.getParameter(DURATION_PARAM)));
            } catch (NumberFormatException e) {
                log.warning("Could not parse " + DURATION_PARAM + ": "
                        + req.getParameter(DURATION_PARAM));
                setSurveyDuration(0L);
            }
        }
        if (req.getParameter(FORM_VER_PARAM) != null) {
            try {
                setFormVersion(Double.valueOf(req.getParameter(FORM_VER_PARAM)));
            } catch (NumberFormatException e) {
                log.warning("Could not parse " + FORM_VER_PARAM + ": "
                        + req.getParameter(FORM_VER_PARAM));
                setFormVersion(0.0);
            }
        }
    }

    /**
     * @throws UnsupportedEncodingException
     */
    private void handleQuestionIdParam(String[] answers) throws UnsupportedEncodingException {
        if (answers != null) {
            for (String answer : answers) {
                // answer: 242334|0=abc|1=def|2=ghi|type=VALUE
                // The iteration responses are also URLEncoded in order to escape pipe
                // characters
                String[] parts = URLDecoder.decode(answer, "UTF-8").split("\\|");
                Map<Integer, String> iterations = new HashMap<>();
                Long questionId = Long.valueOf(parts[0]);
                String type = null;
                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i];
                    String[] keyValue = part.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String val = keyValue[1];

                        switch (key) {
                            case "type":
                                type = val;
                                break;
                            default:
                                // key is the iteration and value the response
                                iterations.put(Integer.valueOf(key),
                                        URLDecoder.decode(val, "UTF-8"));
                                break;
                        }
                    }
                }

                if (questionId != null && type != null) {

                    for (Entry<Integer, String> iterationEntry : iterations.entrySet()) {
                        putResponse(questionId, iterationEntry.getKey(),
                                iterationEntry.getValue(),
                                type);
                    }
                } else {
                    log.log(Level.WARNING, "Could not parse \"" + answer
                            + "\" as RawDataImportRequest");
                }
            }
        }
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getSubmitter() {
        return submitter;
    }

    public Long getSurveyedLocaleId() {
        return surveyedLocaleId;
    }

    public void setSurveyedLocaleId(Long surveyedLocaleId) {
        this.surveyedLocaleId = surveyedLocaleId;
    }

    public void setSurveyDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSurveyDuration() {
        return duration;
    }

    public Double getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Double formVersion) {
        this.formVersion = formVersion;
    }

}
