/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.analytics;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPointMappingHistory;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;

/**
 * updates access points based on the old/new values passed in via the logical change record.
 * 
 * @author Christopher Fagiani
 */
public class AccessPointUpdater implements DataSummarizer {
    private static Logger logger = Logger.getLogger(AccessPointUpdater.class
            .getName());
    private static final String IDENTIFIER_ATTR = "communityCode";
    private SurveyAttributeMappingDao mappingDao;
    private AccessPointDao apDao;
    private SurveyInstanceDAO instanceDao;

    public AccessPointUpdater() {
        mappingDao = new SurveyAttributeMappingDao();
        apDao = new AccessPointDao();
        instanceDao = new SurveyInstanceDAO();
    }

    @Override
    public String getCursor() {
        // no-op
        return null;
    }

    /**
     * populates a DataChangeRecord from the input value passed in and uses it to determine whether
     * or not anything needs to be done: if the change to the access point included a change to the
     * country or community, then it will decrement the count from the AccessPointSummary for the
     * old value then increment the count for the new.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        DataChangeRecord lcr = new DataChangeRecord(value);
        String[] ids = lcr.getId().split("\\|");
        if (ids.length == 4) {
            Long surveyId = new Long(ids[0]);
            Long questionId = new Long(ids[1]);
            Long instanceId = new Long(ids[2]);
            Long mappingId = new Long(ids[3]);
            AccessPointMappingHistory apmh = new AccessPointMappingHistory();
            apmh.setSurveyId(surveyId);
            apmh.setQuestionId(questionId);
            apmh.setSurveyInstanceId(instanceId);
            apmh.setSource(this.getClass().getName());
            apmh.setSurveyResponse(value);

            SurveyAttributeMapping identifierMapping = mappingDao
                    .findMappingForAttribute(surveyId, IDENTIFIER_ATTR);
            SurveyAttributeMapping questionMapping = mappingDao
                    .getByKey(mappingId);
            if (identifierMapping != null && questionMapping != null) {
                QuestionAnswerStore changedAnswer = null;
                List<QuestionAnswerStore> answers = instanceDao
                        .listQuestionAnswerStore(instanceId, null);
                if (answers != null) {
                    String communityCode = null;
                    if (questionId == Long.parseLong(identifierMapping
                            .getSurveyQuestionId())) {
                        communityCode = lcr.getOldVal();
                    } else {
                        for (QuestionAnswerStore qas : answers) {
                            if (qas.getQuestionID().equals(
                                    identifierMapping.getSurveyQuestionId())) {
                                communityCode = qas.getValue();
                            } else if (qas.getQuestionID().equals(
                                    questionId.toString())) {
                                changedAnswer = qas;
                            }
                            if (communityCode != null && changedAnswer != null) {
                                break;
                            }
                        }
                    }
                    if (communityCode != null && changedAnswer != null) {
                        List<AccessPoint> pointList = apDao.searchAccessPoints(
                                null, communityCode, null, null, null, null,
                                null, null, "collectionDate", "desc", null,
                                null);
                        if (pointList != null && pointList.size() > 0) {
                            AccessPoint point = pointList.get(0);
                            try {
                                AccessPointHelper.setAccessPointField(point,
                                        changedAnswer, questionMapping, apmh);
                                logger.info("Estimated pop is: "
                                        + point.getExtimatedPopulation());
                                apDao.save(point);
                            } catch (Exception e) {
                                logger.log(Level.SEVERE,
                                        "Could not update AP field", e);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
