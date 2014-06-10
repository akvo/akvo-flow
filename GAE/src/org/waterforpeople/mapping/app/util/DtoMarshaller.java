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

import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.web.dto.OGRFeatureDto;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class DtoMarshaller {

    public static <T extends BaseDomain, U extends BaseDto> void copyToCanonical(
            T canonical, U dto) {
        try {
            configureConverters();
            BeanUtils.copyProperties(canonical, dto);
            if (dto.getKeyId() != null) {
                // by default, the JDO key kind uses the Simple name
                canonical.setKey(KeyFactory.createKey(canonical.getClass()
                        .getSimpleName(), dto.getKeyId()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends BaseDomain, U extends BaseDto> void copyToDto(
            T canonical, U dto) {
        try {
            configureConverters();
            BeanUtils.copyProperties(dto, canonical);
            if (canonical.getKey() != null) {
                dto.setKeyId(canonical.getKey().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sets up the converters that this marshaller should use
     */
    private static void configureConverters() {
        String pattern = "MM/dd/yy";
        Locale locale = Locale.getDefault();
        DateLocaleConverter converter = new DateLocaleConverter(locale, pattern);
        converter.setLenient(true);
        ConvertUtils.register(converter, java.util.Date.class);

        TypeEnumConverter enumConverter = new TypeEnumConverter();
        ConvertUtils.register(enumConverter, Question.Type.class);
        ConvertUtils.register(enumConverter, QuestionDto.QuestionType.class);
        ConvertUtils.register(enumConverter, AccessPoint.Status.class);
        ConvertUtils.register(enumConverter, AccessPoint.AccessPointType.class);
        ConvertUtils.register(enumConverter, UnitOfMeasure.UnitOfMeasureSystem.class);
        ConvertUtils.register(enumConverter, UnitOfMeasure.UnitOfMeasureType.class);
        ConvertUtils.register(enumConverter, QuestionHelpMedia.Type.class);
        ConvertUtils.register(enumConverter, QuestionHelpDto.Type.class);
        ConvertUtils.register(enumConverter, OGRFeatureDto.FeatureType.class);
        ConvertUtils.register(enumConverter, Survey.Status.class);
        ConvertUtils.register(enumConverter, Survey.Sector.class);

        // Resetting default values from zero to null
        ConvertUtils.register(new DoubleConverter(null), Double.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);

        DatastoreTextConverter textConverter = new DatastoreTextConverter();
        ConvertUtils.register(textConverter, Text.class);
        ConvertUtils.register(textConverter, String.class);
    }

}
