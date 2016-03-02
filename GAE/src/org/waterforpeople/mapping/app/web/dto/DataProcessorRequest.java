/*
 *  Copyright (C) 2010-2015 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Handles requests to the DataProcessing Rest Service
 *
 * @author Christopher Fagiani
 */
public class DataProcessorRequest extends RestRequest {
    private static final long serialVersionUID = -4553663867954174523L;
    public static final String PROJECT_FLAG_UPDATE_ACTION = "projectFlagUpdate";
    public static final String REBUILD_QUESTION_SUMMARY_ACTION = "rebuildQuestionSummary";
    public static final String IMPORT_REMOTE_SURVEY_ACTION = "importRemoteSurvey";
    public static final String COPY_SURVEY = "copySurvey";
    public static final String COPY_QUESTION_GROUP = "copyQuestionGroup";
    public static final String FIX_OPTIONS2VALUES_ACTION = "fixOptions2Values";
    public static final String FIX_DUPLICATE_OTHER_TEXT_ACTION = "fixDuplicateOtherText";
    public static final String FIX_QUESTIONGROUP_DEPENDENCIES_ACTION = "fixQuestionGroupDepencencies";
    public static final String ASSEMBLE_DATAPOINT_NAME = "assembleDatapointName";
    public static final String DELETE_DUPLICATE_QAS = "deleteDuplicatedQAS";
    public static final String RECOMPUTE_LOCALE_CLUSTERS = "recomputeLocaleClusters";
    public static final String SURVEY_INSTANCE_SUMMARIZER = "surveyInstanceSummarizer";
    public static final String SURVEY_RESPONSE_COUNT = "surveyResponseCount";
    public static final String TRIM_OPTIONS = "trimOptions";
    public static final String RESCORE_AP_ACTION = "rescoreAp";
    public static final String SOURCE_PARAM = "source";
    public static final String COUNTRY_PARAM = "country";
    public static final String SURVEY_GROUP_PARAM = "surveyGroupId";
    public static final String SURVEY_ID_PARAM = "surveyId";
    public static final String QUESTION_GROUP_ID_PARAM = "questionGroupId";
    public static final String COUNTER_ID_PARAM = "summaryCounterId";
    public static final String SURVEY_INSTANCE_PARAM = "surveyInstanceId";
    public static final String LOCALE_ID_PARAM = "surveyedLocaleId";
    public static final String QAS_ID_PARAM = "qasId";
    public static final String DELTA_PARAM = "delta";
    public static final String API_KEY_PARAM = "apiKey";
    public static final String OFFSET_PARAM = "offset";
    public static final String DEPENDENT_QUESTION_PARAM = "depedentQuestionId";
    public static final String RETRY_PARAM = "retry";
    public static final String CHANGE_LOCALE_TYPE_ACTION = "changeLocaleType";
    public static final String ADD_SURVEY_INSTANCE_TO_LOCALES_ACTION = "addSurveyInstanceToLocales";
    public static final String ADD_CREATION_SURVEY_ID_TO_LOCALE = "addCreationSurveyIdToLocale";
    public static final String ADD_TRANSLATION_FIELDS = "addTranslationFields";
    public static final String RECREATE_LOCALES = "recreateLocales";
    public static final String POP_QUESTION_ORDER_FIELDS_ACTION = "populateQuestionOrders";
    public static final String POPULATE_MONITORING_FIELDS_LOCALE_ACTION = "populateMonitoringFieldsLocale";
    public static final String CREATE_NEW_IDENTIFIERS_LOCALES_ACTION = "createNewIdentifiersLocales";
    public static final String DELETE_SURVEY_INSTANCE_ACTION = "deleteSurveyInstance";
    public static final String DELETE_CASCADE_NODES = "deleteCascadeNodes";
    public static final String CASCADE_RESOURCE_ID = "cascadeResourceId";
    public static final String PARENT_NODE_ID = "parentNodeId";
    public static final int MAX_TASK_RETRIES = 3;

    private String country;
    private String source;
    private Long surveyGroupId;
    private Long surveyId;
    private Long surveyInstanceId;
    private Long questionGroupId;
    private Long qasId;
    private Long surveyedLocaleId;
    private Integer delta;
    private String apiKey;
    private Long offset = 0L;
    private Long summaryCounterId;
    private Long cascadeResourceId = 0L;
    private Long parentNodeId = null;
    private List<Long> dependentQuestionIds;
    private int retry = 0;

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        country = req.getParameter(COUNTRY_PARAM);
        source = req.getParameter(SOURCE_PARAM);
        if (req.getParameter(SURVEY_ID_PARAM) != null) {
            try {
                surveyId = new Long(req.getParameter(SURVEY_ID_PARAM).trim());
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, SURVEY_ID_PARAM
                                + " must be an integer"));
            }
        }
        if (req.getParameter(QUESTION_GROUP_ID_PARAM) != null) {
            try {
                questionGroupId = new Long(req.getParameter(QUESTION_GROUP_ID_PARAM).trim());
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, QUESTION_GROUP_ID_PARAM
                                + " must be a number"));
            }
        }
        if (req.getParameter(SURVEY_INSTANCE_PARAM) != null) {
            try {
                setSurveyInstanceId(new Long(req.getParameter(SURVEY_INSTANCE_PARAM).trim()));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, SURVEY_INSTANCE_PARAM
                                + " must be an integer"));
            }
        }
        if (req.getParameter(QAS_ID_PARAM) != null) {
            try {
                setQasId(new Long(req.getParameter(QAS_ID_PARAM).trim()));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, QAS_ID_PARAM
                                + " must be an integer"));
            }
        }

        if (req.getParameter(DELTA_PARAM) != null) {
            try {
                setDelta(new Integer(req.getParameter(DELTA_PARAM).trim()));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, DELTA_PARAM
                                + " must be an integer"));
            }
        }

        if (req.getParameter(API_KEY_PARAM) != null) {
            setApiKey(req.getParameter(API_KEY_PARAM).trim());
        }

        if (req.getParameter(OFFSET_PARAM) != null) {
            setOffset(Long.valueOf(req.getParameter(OFFSET_PARAM).trim()));
        }

        if (req.getParameter(COUNTER_ID_PARAM) != null) {
            try {
                setSummaryCounterId(new Long(req.getParameter(COUNTER_ID_PARAM)));
            } catch (Exception e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE, COUNTER_ID_PARAM
                                + " must be a number"));
            }
        }

        if (req.getParameter(DEPENDENT_QUESTION_PARAM) != null) {
            String[] idStrings = req.getParameterValues(DEPENDENT_QUESTION_PARAM);
            dependentQuestionIds = new ArrayList<Long>();
            try {
                for (int i = 0; i < idStrings.length; i++) {
                    if (StringUtils.isNotBlank(idStrings[i])) {
                        dependentQuestionIds.add(Long.parseLong(idStrings[i]));
                    }
                }
            } catch (NumberFormatException e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE, RestError.BAD_DATATYPE_MESSAGE,
                        DEPENDENT_QUESTION_PARAM + " must be a number"));
            }
        }

        if (req.getParameter(RETRY_PARAM) != null) {
            try {
                retry = Integer.valueOf(req.getParameter(RETRY_PARAM));
            } catch (NumberFormatException e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE, RestError.BAD_DATATYPE_MESSAGE,
                        RETRY_PARAM + " must be a number"));
            }
        }

        if (req.getParameter(CASCADE_RESOURCE_ID) != null) {
            try {
                setCascadeResourceId(Long.valueOf(req.getParameter(CASCADE_RESOURCE_ID)));
            } catch (NumberFormatException e) {
                // no-op
            }
        }

        if (req.getParameter(PARENT_NODE_ID) != null) {
            try {
                setParentNodeId(Long.valueOf(req.getParameter(PARENT_NODE_ID)));
            } catch (NumberFormatException e) {
                // no-op
            }
        }
        
        if (req.getParameter(LOCALE_ID_PARAM) != null) {
            try {
                setSurveyedLocaleId(Long.valueOf(req.getParameter(LOCALE_ID_PARAM)));
            } catch (NumberFormatException e) {
                // no-op
            }
        }
        
        if (req.getParameter(SURVEY_GROUP_PARAM) != null) {
            try {
                setSurveyGroupId(Long.valueOf(req.getParameter(SURVEY_GROUP_PARAM)));
            } catch (NumberFormatException e) {
                // no-op
            }
        }
    }

    @Override
    protected void populateErrors() {
        // no-op

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public Long getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Long questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public Long getQasId() {
        return qasId;
    }

    public void setQasId(Long qasId) {
        this.qasId = qasId;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getSummaryCounterId() {
        return summaryCounterId;
    }

    public void setSummaryCounterId(Long summaryCounterId) {
        this.summaryCounterId = summaryCounterId;
    }

    public Long getCascadeResourceId() {
        return cascadeResourceId;
    }

    public void setCascadeResourceId(Long cascadeResourceId) {
        this.cascadeResourceId = cascadeResourceId;
    }

    public Long getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(Long parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public List<Long> getDependentQuestionIds() {
        return dependentQuestionIds;
    }

    public void setDependentQuestionIds(List<Long> dependentQuestionIds) {
        this.dependentQuestionIds = dependentQuestionIds;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
    
    public Long getSurveyedLocaleId() {
        return surveyedLocaleId;
    }

    public void setSurveyedLocaleId(Long surveyedLocaleId) {
        this.surveyedLocaleId = surveyedLocaleId;
    }
    
    public Long getSurveyGroupId() {
        return surveyGroupId;
    }

    public void setSurveyGroupId(Long surveyGroupId) {
        this.surveyGroupId = surveyGroupId;
    }
    
}
