/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class SurveyInstanceDto extends BaseDto {

    private static final long serialVersionUID = 8484584703637564931L;

    private Long userID;

    private Date collectionDate;

    private List<QuestionAnswerStoreDto> questionAnswersStore;

    private Long surveyId;

    /**
     * Initialize surveyalTime to enhance backwards compatibility
     */
    private Long surveyalTime = 0L;

    private String submitterName;
    private String deviceIdentifier;
    private String surveyCode;
    private String approvedFlag;
    private String approximateLocationFlag;
    private Long surveyedLocaleId;
    private String surveyedLocaleIdentifier;
    private String surveyedLocaleDisplayName;

    public String getApproximateLocationFlag() {
        return approximateLocationFlag;
    }

    public void setApproximateLocationFlag(String approximateLocationFlag) {
        this.approximateLocationFlag = approximateLocationFlag;
    }

    public String getApprovedFlag() {
        return approvedFlag;
    }

    public void setApprovedFlag(String approvedFlag) {
        this.approvedFlag = approvedFlag;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public List<QuestionAnswerStoreDto> getQuestionAnswersStore() {
        return questionAnswersStore;
    }

    public void setQuestionAnswersStore(
            List<QuestionAnswerStoreDto> questionAnswersStore) {
        this.questionAnswersStore = questionAnswersStore;
    }

    public void addQuestionAnswerStore(QuestionAnswerStoreDto item) {
        if (questionAnswersStore == null)
            questionAnswersStore = new ArrayList<QuestionAnswerStoreDto>();
        questionAnswersStore.add(item);
    }

    public void setSurveyCode(String surveyCode) {
        this.surveyCode = surveyCode;
    }

    public String getSurveyCode() {
        return surveyCode;
    }

    public void setSurveyalTime(Long surveyalTime) {
        this.surveyalTime = surveyalTime;
    }

    public Long getSurveyalTime() {
        return surveyalTime;
    }

    public Long getSurveyedLocaleId() {
        return surveyedLocaleId;
    }

    public void setSurveyedLocaleId(Long surveyedLocaleId) {
        this.surveyedLocaleId = surveyedLocaleId;
    }

    public String getSurveyedLocaleIdentifier() {
        return surveyedLocaleIdentifier;
    }

    public void setSurveyedLocaleIdentifier(String surveyedLocaleIdentifier) {
        this.surveyedLocaleIdentifier = surveyedLocaleIdentifier;
    }

    public String getSurveyedLocaleDisplayName() {
        return surveyedLocaleDisplayName;
    }

    public void setSurveyedLocaleDisplayName(String surveyedLocaleDisplayName) {
        this.surveyedLocaleDisplayName = surveyedLocaleDisplayName;
    }
}
