/*
 * Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.flow.rest.form;

import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.TreeMap;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

/**
 * Maps a Survey object from SurveyDto
 */
public class FormMapper {
    private final QuestionGroupMapper questionGroupMapper;

    public FormMapper(QuestionGroupMapper questionGroupMapper) {
        this.questionGroupMapper = questionGroupMapper;
    }

    Survey mapFormFromDto(SurveyDto surveyDto) {
        Survey form = new Survey();
        form.setKey(KeyFactory.createKey("Survey", surveyDto.getKeyId()));
        form.setCode(surveyDto.getCode());
        form.setName(surveyDto.getName());
        form.setVersion(Double.parseDouble(surveyDto.getVersion()));
        form.setDesc(surveyDto.getDescription());
        String status = surveyDto.getStatus();
        if (status != null) {
            form.setStatus(Survey.Status.valueOf(status));
        }
        form.setPath(surveyDto.getPath());
        form.setSurveyGroupId(surveyDto.getSurveyGroupId());
        form.setDefaultLanguageCode(surveyDto.getDefaultLanguageCode());
        form.setRequireApproval(surveyDto.getRequireApproval());
        form.setCreatedDateTime(surveyDto.getCreatedDateTime());
        form.setLastUpdateDateTime(surveyDto.getLastUpdateDateTime());
        form.setAncestorIds(surveyDto.getAncestorIds());
        TreeMap<Integer, QuestionGroup> groupMap = questionGroupMapper.mapGroups(surveyDto.getQuestionGroupList());
        form.setQuestionGroupMap(groupMap);
        return form;
    }
}
