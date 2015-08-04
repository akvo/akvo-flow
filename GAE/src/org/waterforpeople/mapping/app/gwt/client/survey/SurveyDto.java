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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class SurveyDto extends BaseDto {
    private static final long serialVersionUID = 6593732844403807030L;
    private String name;
    private String code;
    private String version;
    private String description;
    private String status;
    private String sector;
    private List<QuestionGroupDto> questionGroupList;
    private String path;
    private Long surveyGroupId = null;
    private Long instanceCount;
    private String pointType;
    private String defaultLanguageCode;
    private Boolean requireApproval;
    private Date createdDateTime;
    private Date lastUpdateDateTime;
    private Long sourceId = null;
    private List<Long> ancestorIds;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    public void addQuestionGroup(QuestionGroupDto questionGroup) {
        if (questionGroupList == null) {
            questionGroupList = new ArrayList<QuestionGroupDto>();
        }
        questionGroupList.add(questionGroup);
    }

    public List<QuestionGroupDto> getQuestionGroupList() {
        return questionGroupList;
    }

    public void setQuestionGroupList(List<QuestionGroupDto> questionGroupList) {
        this.questionGroupList = questionGroupList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setSurveyGroupId(Long surveyGroupId) {
        this.surveyGroupId = surveyGroupId;
    }

    public Long getSurveyGroupId() {
        return surveyGroupId;
    }

    public void setInstanceCount(Long instanceCount) {
        this.instanceCount = instanceCount;
    }

    public Long getInstanceCount() {
        return instanceCount;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public List<Long> getAncestorIds() {
        return ancestorIds;
    }

    public void setAncestorIds(List<Long> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }

}
