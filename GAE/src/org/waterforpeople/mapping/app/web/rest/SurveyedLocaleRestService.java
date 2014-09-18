/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

@Controller
@RequestMapping("/surveyed_locales")
public class SurveyedLocaleRestService {

    @Inject
    SurveyedLocaleDao surveyedLocaleDao;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listQuestions(
            @RequestParam(value = "surveyGroupId", defaultValue = "")
            Long surveyGroupId,
            @RequestParam(value = "identifier", defaultValue = "")
            String identifier,
            @RequestParam(value = "displayName", defaultValue = "")
            String displayName) {

        Map<String, Object> response = new HashMap<String, Object>();

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        List<SurveyedLocale> sls = new ArrayList<SurveyedLocale>();
        List<SurveyedLocaleDto> locales = new ArrayList<SurveyedLocaleDto>();

        if (identifier != null && !"".equals(identifier)) {
            sls = surveyedLocaleDao.listLocalesByCode(identifier, false);
        } else if (displayName != null && !"".equals(displayName)) {
            sls = surveyedLocaleDao.listLocalesByDisplayName(displayName);
        } else if (surveyGroupId != null) {
            sls = surveyedLocaleDao.listLocalesBySurveyGroupAndDate(
                    surveyGroupId, null, 20);
        }

        for (SurveyedLocale sl : sls) {
            SurveyedLocaleDto dto = new SurveyedLocaleDto();
            DtoMarshaller.copyToDto(sl, dto);
            locales.add(dto);
        }

        response.put("surveyed_locales", locales);
        response.put("meta", statusDto);
        return response;
    }
}
