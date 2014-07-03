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

package com.gallatinsystems.survey.domain;

import java.util.HashMap;
import java.util.TreeMap;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Survey are a collection of questionGroups (that, in turn, have collections of questions). A
 * survey belongs to exactly 1 survey group. Surveys can have a default language that indicates what
 * language is considered primary.
 */
@PersistenceCapable
public class Survey extends BaseDomain {

    private static final long serialVersionUID = -8638039212962768687L;
    @NotPersistent
    private HashMap<String, Translation> translationMap;
    private String code = null;
    private String name = null;
    private String desc = null;
    private Status status = null;
    private Sector sector = null;
    @NotPersistent
    private TreeMap<Integer, QuestionGroup> questionGroupMap = null;
    private Double version = null;
    @NotPersistent
    private Long instanceCount;
    private String path = null;
    private Long surveyGroupId;
    private String pointType;
    private String defaultLanguageCode;
    private Boolean requireApproval;

    public enum Status {
        PUBLISHED, NOT_PUBLISHED, IMPORTED, VERIFIED, COPYING
    };

    public enum Sector {
        WASH, EDUC, ECONDEV, HEALTH, ICT, FOODSEC, OTHER
    };

    public Survey() {
        questionGroupMap = new TreeMap<Integer, QuestionGroup>();
        requireApproval = false;
    }

    public void incrementVersion() {
        if(version == null) {
            version = Double.valueOf("1.0");
        } else {
            version++;
        }
    }

    public Long getSurveyGroupId() {
        return surveyGroupId;
    }

    public void setSurveyGroupId(Long surveyGroupId) {
        this.surveyGroupId = surveyGroupId;
    }

    public void setInstanceCount(Long instanceCount) {
        this.instanceCount = instanceCount;
    }

    public Long getInstanceCount() {
        return instanceCount;
    }

    public HashMap<String, Translation> getTranslationMap() {
        return translationMap;
    }

    public void setTranslationMap(HashMap<String, Translation> translationMap) {
        this.translationMap = translationMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public void setQuestionGroupMap(
            TreeMap<Integer, QuestionGroup> questionGroupMap) {
        this.questionGroupMap = questionGroupMap;
    }

    public TreeMap<Integer, QuestionGroup> getQuestionGroupMap() {
        return questionGroupMap;
    }

    public void addQuestionGroup(Integer order, QuestionGroup questionGroup) {

        questionGroupMap.put(order, questionGroup);
    }

    public void addQuestionGroup(QuestionGroup questionGroup) {
        addQuestionGroup(
                questionGroup.getOrder() != null ? questionGroup.getOrder()
                        : getQuestionGroupMap().size() + 1, questionGroup);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void addTranslation(Translation t) {
        if (translationMap == null) {
            translationMap = new HashMap<String, Translation>();
        }
        translationMap.put(t.getLanguageCode(), t);
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public void setDefaultLanguageCode(String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
    }

    public String getDefaultLanguageCode() {
        return defaultLanguageCode;
    }

    public void setRequireApproval(Boolean requireApproval) {
        this.requireApproval = requireApproval;
    }

    public Boolean getRequireApproval() {
        return requireApproval;
    }
}
