/*
 *  Copyright (C) 2010-2015, 2020 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import org.akvo.flow.domain.RootFolder;
import org.akvo.flow.domain.SecuredObject;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;

/**
 * a grouping of surveys.
 */
@PersistenceCapable
public class SurveyGroup extends BaseDomain implements SecuredObject {

    private static final long serialVersionUID = 8941584684617286776L;
    private String name = null;
    private String code = null;
    private Boolean monitoringGroup = false;
    private Long newLocaleSurveyId;
    private Long parentId;
    private String description = null;
    private String path = null;
    private ProjectType projectType;
    private String defaultLanguageCode;
    private Boolean published;
    private Boolean requireDataApproval = false;
    private Long dataApprovalGroupId;
    private Boolean isTemplate;

    @NotPersistent
    private HashMap<String, Translation> altTextMap;
    @NotPersistent
    private List<Survey> surveyList = null;

    @NotPersistent
    private List<SurveyGroup> childFolders;

    @NotPersistent
    private List<Survey> childForms;

    public enum ProjectType {
        PROJECT_FOLDER, PROJECT
    }

    public HashMap<String, Translation> getAltTextMap() {
        return altTextMap;
    }

    public void setAltTextMap(HashMap<String, Translation> altTextMap) {
        this.altTextMap = altTextMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSurveyList(List<Survey> surveyList) {
        this.surveyList = surveyList;
    }

    public List<Survey> getSurveyList() {
        return surveyList;
    }

    public void addSurvey(Survey survey) {
        if (surveyList == null)
            surveyList = new ArrayList<Survey>();
        surveyList.add(survey);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getMonitoringGroup() {
        return monitoringGroup;
    }

    public void setMonitoringGroup(Boolean monitoringGroup) {
        this.monitoringGroup = monitoringGroup;
    }

    public Long getNewLocaleSurveyId() {
        return newLocaleSurveyId;
    }

    public void setNewLocaleSurveyId(Long newLocaleSurveyId) {
        this.newLocaleSurveyId = newLocaleSurveyId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parent) {
        this.parentId = parent;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getDefaultLanguageCode() {
        return defaultLanguageCode;
    }

    public void setDefaultLanguageCode(String defaultLanuageCode) {
        this.defaultLanguageCode = defaultLanuageCode;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getRequireDataApproval() {
        return requireDataApproval;
    }

    public void setRequireDataApproval(Boolean requireDataApproval) {
        this.requireDataApproval = requireDataApproval;
    }

    public Long getDataApprovalGroupId() {
        return dataApprovalGroupId;
    }

    public void setDataApprovalGroupId(Long dataApprovalGroupId) {
        this.dataApprovalGroupId = dataApprovalGroupId;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
    }

    @Override
    public SecuredObject getParentObject() {
        if (parentId == null) {
            return null;
        } else if (Constants.ROOT_FOLDER_ID.equals(parentId)) {
            return new RootFolder();
        }

        return new SurveyGroupDAO().getByKey(parentId);
    }

    @Override
    public Long getObjectId() {
        if (key == null) {
            return null;
        }
        return key.getId();
    }

    @Override
    public List<Long> listAncestorIds() {
        return ancestorIds;
    }

    @Override
    public List<BaseDomain> updateAncestorIds(boolean cascade) {
        if (ancestorIds == null || key == null) {
            return Collections.emptyList();
        }

        List<BaseDomain> updatedEntities = new ArrayList<BaseDomain>();
        List<Long> childAncestorIds = new ArrayList<Long>(ancestorIds);
        childAncestorIds.add(key.getId());

        if (childFolders != null) {
            for (SurveyGroup sg : childFolders) {
                sg.setAncestorIds(childAncestorIds);
                if (cascade) {
                    SurveyUtils.setChildObjects(sg);
                    updatedEntities.addAll(sg.updateAncestorIds(cascade));
                }
            }
            updatedEntities.addAll(childFolders);
        }

        if (childForms != null) {
            for (Survey s : childForms) {
                s.setAncestorIds(childAncestorIds);
            }
            updatedEntities.addAll(childForms);
        }
        return updatedEntities;
    }

    public List<SurveyGroup> getChildFolders() {
        return childFolders;
    }

    public void setChildFolders(List<SurveyGroup> childFolders) {
        this.childFolders = childFolders;
    }

    public List<Survey> getChildForms() {
        return childForms;
    }

    public void setChildForms(List<Survey> childForms) {
        this.childForms = childForms;
    }
}
