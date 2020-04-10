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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/surveyed_locale_counts")
public class SurveyedLocaleCountRestService {
    private SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    private SurveyGroupDAO surveyGroupDAO = new SurveyGroupDAO();

    @RequestMapping(method = RequestMethod.GET, value = "/{surveyGroupId}")
    @ResponseBody
    public Map<String, Object> getCount(@PathVariable String surveyGroupId) {

        long id = Long.parseLong(surveyGroupId);
        SurveyGroup surveyGroup = surveyGroupDAO.getByKey(id);

        if (surveyGroup == null) {
            throw new HttpMessageNotReadableException("SurveyGroup with ID " + surveyGroupId + " doesn't exist");
        }

        Map<String, Object> response = new HashMap<>();

        Long count = surveyedLocaleDao.countBySurveyGroupId(id);

        Map<String, Object> surveyedLocaleCount = new HashMap<>();
        surveyedLocaleCount.put("keyId", surveyGroupId);
        surveyedLocaleCount.put("count", count);

        response.put("surveyed_locale_count", surveyedLocaleCount);
        return response;
    }
}
