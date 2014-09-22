/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;

public class SurveyInstancePayload implements Serializable {

    private static final long serialVersionUID = 8812994636904876896L;
    SurveyInstanceDto survey_instance = null;

    public SurveyInstanceDto getSurvey_instance() {
        return survey_instance;
    }

    public void setSurvey_instance(SurveyInstanceDto survey_instance) {
        this.survey_instance = survey_instance;
    }
}
