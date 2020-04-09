/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/surveyed_locale_count")
public class SurveyedLocaleCountRestService {
    private SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    private SurveyGroupDAO surveyGroupDAO = new SurveyGroupDAO();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getCount(@RequestParam(value = "surveyGroupId", defaultValue = "") Long surveyGroupId) {

        SurveyGroup surveyGroup = new SurveyGroupDAO().getByKey(surveyGroupId);

        if (surveyGroup == null) {
            throw new HttpMessageNotReadableException("SurveyGroup with ID " + surveyGroupId + " doesn't exist");
        }

        Map<String, Object> response = new HashMap<String, Object>();

        RestStatusDto statusDto = new RestStatusDto();

        Long count = surveyedLocaleDao.countBySurveyGroupId(surveyGroupId);

        response.put("meta", statusDto);
        response.put("surveyedLocaleCount", count);
        return response;
    }
}
