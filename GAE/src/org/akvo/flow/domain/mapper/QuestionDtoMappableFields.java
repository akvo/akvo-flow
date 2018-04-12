/*
 *  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.domain.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;

public interface QuestionDtoMappableFields {

    String text = null;
    QuestionDto.QuestionType type =  QuestionDto.QuestionType.FREE_TEXT;
    OptionContainerDto optionContainerDto = null;
    List<QuestionHelpDto> questionHelpList= new ArrayList<>();
    String tip = null;
    String optionList = null;
    List<Long> questionOptions = null;
    Boolean mandatoryFlag = null;
    Boolean dependentFlag = null;
    Boolean localeNameFlag = null;
    Boolean localeLocationFlag = null;
    Boolean geoLocked = null;
    Boolean requireDoubleEntry = null;
    Long dependentQuestionId = null;
    String dependentQuestionAnswer = null;
    Long cascadeResourceId = null;
    String caddisflyResourceUuid = null;
    Long metricId = null;
    //QuestionDependencyDto questionDependency = null;
    Long surveyId = null;
    String questionId = null;
    Long questionGroupId = null;
    Boolean collapseable = null;
    Boolean immutable = null;
    Map<String, TranslationDto> translationMap = new HashMap<>();
    String path = null;
    Integer order = null;
    Boolean allowMultipleFlag = null;
    Boolean allowOtherFlag = null;
    Boolean allowDecimal = null;
    Boolean allowSign = null;
    Boolean allowExternalSources = null;
    Double minVal = null;
    Double maxVal = null;
    Boolean isName = null;
    Long sourceId = null;
    List<String> levelNames = null;
    Boolean allowPoints = null;
    Boolean allowLine = null;
    Boolean allowPolygon = null;


}
