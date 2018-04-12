/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;

public class QuestionGroupListPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    List<QuestionGroupDto> question_groups = null;

    public List<QuestionGroupDto> getQuestion_groups() {
        return question_groups;
    }

    public void setQuestion_groups(List<QuestionGroupDto> question_groups) {
        this.question_groups = question_groups;
    }
}
