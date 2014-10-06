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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * a grouping of surveys.
 */
@PersistenceCapable
public class SurveyGroup extends BaseDomain {

    private static final long serialVersionUID = 8941584684617286776L;
    private String name = null;
    private String code = null;
    private Boolean monitoringGroup = false;
    private Long newLocaleSurveyId;
    private Long parent; // parent survey group id
    private String description = null;
    private ProjectType projectType;

    @NotPersistent
    private HashMap<String, Translation> altTextMap;
    @NotPersistent
    private List<Survey> surveyList = null;

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

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

}
