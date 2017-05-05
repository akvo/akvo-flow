/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyGroup.PrivacyLevel;
import com.gallatinsystems.survey.domain.SurveyGroup.ProjectType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyGroupDto extends BaseDto {

    private static final long serialVersionUID = -2235565143615667202L;

    private String description;
    private String name;
    private String code;
    private String path;
    private Boolean monitoringGroup;
    private Long newLocaleSurveyId;
    private Date createdDateTime;
    private Date lastUpdateDateTime;
    private ProjectType projectType;
    private Long parentId;
    private String defaultLanguageCode;
    private PrivacyLevel privacyLevel;
    private Boolean published;
    private Boolean requireDataApproval;
    private Long dataApprovalGroupId;
    private List<Long> ancestorIds;

    private ArrayList<Long> surveyList = null;

    private SurveyGroup surveyGroup;

    public SurveyGroupDto() {
    }

    public SurveyGroupDto(SurveyGroup sg) {
        this.surveyGroup = sg;
    }

    public String getDescription() {
        if (surveyGroup != null) {
            return surveyGroup.getDescription();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        if (surveyGroup != null) {
            return surveyGroup.getCode();
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        if (surveyGroup != null) {
            return surveyGroup.getPath();
        }

        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreatedDateTime() {
        if (surveyGroup != null) {
            return surveyGroup.getCreatedDateTime();
        }
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getLastUpdateDateTime() {
        if (surveyGroup != null) {
            return surveyGroup.getLastUpdateDateTime();
        }
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    public void setSurveyList(ArrayList<Long> surveyList) {
        this.surveyList = surveyList;
    }

    public ArrayList<Long> getSurveyList() {
        return surveyList;
    }

    public void addSurvey(Long surveyId) {
        if (surveyList == null) {
            surveyList = new ArrayList<Long>();
        }
        surveyList.add(surveyId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (surveyGroup != null) {
            return surveyGroup.getName();
        }
        return name;
    }

    public Boolean getMonitoringGroup() {
        if (surveyGroup != null) {
            return surveyGroup.getMonitoringGroup();
        }
        return monitoringGroup;
    }

    public void setMonitoringGroup(Boolean monitoringGroup) {
        this.monitoringGroup = monitoringGroup;
    }

    public Long getNewLocaleSurveyId() {
        if (surveyGroup != null) {
            return surveyGroup.getNewLocaleSurveyId();
        }
        return newLocaleSurveyId;
    }

    public void setNewLocaleSurveyId(Long newLocaleSurveyId) {
        this.newLocaleSurveyId = newLocaleSurveyId;
    }

    public ProjectType getProjectType() {
        if (surveyGroup != null) {
            return surveyGroup.getProjectType();
        }
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public Long getParentId() {
        if (surveyGroup != null) {
            return surveyGroup.getParentId();
        }
        return parentId;
    }

    public void setParentId(Long parent) {
        this.parentId = parent;
    }

    public String getDefaultLanguageCode() {
        if (surveyGroup != null) {
            return surveyGroup.getDefaultLanguageCode();
        }
        return defaultLanguageCode;
    }

    public void setDefaultLanguageCode(String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
    }

    public PrivacyLevel getPrivacyLevel() {
        if (surveyGroup != null) {
            return surveyGroup.getPrivacyLevel();
        }
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public Boolean getPublished() {
        if (surveyGroup != null) {
            return surveyGroup.getPublished();
        }
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getRequireDataApproval() {
        if (surveyGroup != null) {
            return surveyGroup.getRequireDataApproval();
        }
        return requireDataApproval;
    }

    public void setRequireDataApproval(Boolean requireDataApproval) {
        this.requireDataApproval = requireDataApproval;
    }

    public Long getDataApprovalGroupId() {
        if (surveyGroup != null) {
            return surveyGroup.getDataApprovalGroupId();
        }
        return dataApprovalGroupId;
    }

    public void setDataApprovalGroupId(Long dataApprovalGroupId) {
        this.dataApprovalGroupId = dataApprovalGroupId;
    }

    public List<Long> getAncestorIds() {
        if (surveyGroup != null) {
            return surveyGroup.getAncestorIds();
        }
        return ancestorIds;
    }

    public void setAncestorIds(List<Long> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }

    @Override
    public Long getKeyId() {
        if (surveyGroup != null && surveyGroup.getKey() != null) {
            return surveyGroup.getKey().getId();
        }
        return super.getKeyId();
    }
}
