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

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Wrapper object allowing persistence of xml representations of surveys
 */
@PersistenceCapable
public class SurveyContainer extends BaseDomain {

    private static final long serialVersionUID = -1445653380398913451L;
    private Long surveyId = null;
    private String description;
    private String notes;

    private com.google.appengine.api.datastore.Text surveyDocument;

    public com.google.appengine.api.datastore.Text getSurveyDocument() {
        return surveyDocument;
    }

    public void setSurveyDocument(
            com.google.appengine.api.datastore.Text surveyDocument) {
        this.surveyDocument = surveyDocument;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

}
