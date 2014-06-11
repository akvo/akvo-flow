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

package org.waterforpeople.mapping.app.util;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.web.dto.OGRFeatureDto;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;

/**
 * converts enumerated types
 * 
 * @author Christopher Fagiani
 */
@SuppressWarnings("rawtypes")
public class TypeEnumConverter extends AbstractConverter {

    @Override
    protected Object convertToType(Class type, Object value) throws Throwable {
        if (value != null) {
            if (type == Question.Type.class) {

                return Question.Type.valueOf(value.toString());

            } else if (type == QuestionDto.QuestionType.class) {

                return QuestionDto.QuestionType.valueOf(value.toString());

            } else if (type == AccessPoint.Status.class) {

                return AccessPoint.Status.valueOf(value.toString());

            } else if (type == AccessPoint.AccessPointType.class) {
                return AccessPoint.AccessPointType.valueOf(value.toString());
            } else if (type == UnitOfMeasure.UnitOfMeasureSystem.class) {
                return UnitOfMeasure.UnitOfMeasureSystem.valueOf(value
                        .toString());
            } else if (type == UnitOfMeasure.UnitOfMeasureType.class) {
                return UnitOfMeasure.UnitOfMeasureType
                        .valueOf(value.toString());
            } else if (type == QuestionHelpMedia.Type.class) {
                return QuestionHelpMedia.Type.valueOf(value.toString());
            } else if (type == QuestionHelpDto.Type.class) {
                return QuestionHelpDto.Type.valueOf(value.toString());
            } else if (type == OGRFeatureDto.FeatureType.class) {
                return OGRFeatureDto.FeatureType.valueOf(value.toString());
            } else if (type == Survey.Status.class) {
                return Survey.Status.valueOf(value.toString());
            }
        }
        return null;
    }

    @Override
    public Object handleMissing(Class type) {
        return null;
    }

    @Override
    protected Class getDefaultType() {
        return QuestionType.class;
    }

}
