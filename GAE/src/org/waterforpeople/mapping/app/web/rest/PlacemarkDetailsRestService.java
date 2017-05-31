/*
 *  Copyright (C) 2012,2017 Stichting Akvo (Akvo Foundation)
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.PlacemarkDetailDto;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

/**
 * This service is providing the details for a particular Placemark, due to the lack of partial
 * loading in Ember-Data See: https://github.com/emberjs/data/issues/51
 */
@Controller
@RequestMapping("/placemark_details")
public class PlacemarkDetailsRestService {

    private static final Logger log = Logger
            .getLogger(PlacemarkDetailsRestService.class.getName());

    private SurveyedLocaleDao localeDao = new SurveyedLocaleDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> getDetails(
            @RequestParam(value = "placemarkId", defaultValue = "")
            String placemarkId) {

        if (StringUtils.isEmpty(placemarkId)) {
            final String msg = "[placemarkId] is a required parameter";
            log.log(Level.SEVERE, msg);
            throw new HttpMessageNotReadableException(msg);
        }

        final SurveyedLocale sl = localeDao.getById(Long.valueOf(placemarkId));

        if (sl == null) {
            final String msg = "placemarkId : " + placemarkId + " not found";
            log.log(Level.SEVERE, msg);
            throw new HttpMessageNotReadableException(msg);
        }

        final Map<String, Object> response = new HashMap<String, Object>();

        response.put("placemark_details", getPlacemarkDetails(sl));

        return response;
    }

    private List<PlacemarkDetailDto> getPlacemarkDetails(SurveyedLocale sl) {
        final List<PlacemarkDetailDto> details = new ArrayList<PlacemarkDetailDto>();
        Integer qgOrder;
        Integer qOrder;

        if (sl.getSurveyalValues() == null) {
            return details;
        }

        for (SurveyalValue sv : sl.getSurveyalValues()) {
            PlacemarkDetailDto pmDto = new PlacemarkDetailDto();
            DtoMarshaller.copyToDto(sv, pmDto);
            pmDto.setPlacemarkId(sl.getKey().getId());
            qgOrder = sv.getQuestionGroupOrder();
            qOrder = sv.getQuestionOrder();
            pmDto.setOrder((qgOrder == null ? 0 : qgOrder) * 1000 + (qOrder == null ? 0 : qOrder));
            details.add(pmDto);
        }

        return details;
    }
}
