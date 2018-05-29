/*
 *  Copyright (C) 2012,2017-2018 Stichting Akvo (Akvo Foundation)
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.PlacemarkDto;
import org.waterforpeople.mapping.app.web.rest.security.AppRole;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

@Controller
@RequestMapping("/placemarks")
public class PlacemarkRestService {
    final int LIMIT_PLACEMARK_POINTS = 2000;
    private static final Logger log = Logger
            .getLogger(PlacemarkRestService.class.getName());

    private SurveyedLocaleDao localeDao = new SurveyedLocaleDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listPlaceMarks(
            @RequestParam(value = "surveyId", defaultValue = "-1") Long surveyId, // default to non-existing surveyId
            @RequestParam(value = "bbString", defaultValue = "") String boundingBoxString,
            @RequestParam(value = "gcLevel", defaultValue = "") Integer gcLevel) {

        log.log(Level.FINE, "received request for: " + boundingBoxString + ", " + gcLevel);

        final List<String> geocells = Arrays.asList(boundingBoxString.split(","));
        final Map<String, Object> response = new HashMap<String, Object>();
        final List<PlacemarkDto> placemarkList = new ArrayList<PlacemarkDto>();
        final List<SurveyedLocale> dataPointList = new ArrayList<>();
        final boolean isAuthorizedUser = isAuthorizedUser();

        if(isAuthorizedUser) {
            dataPointList.addAll(listAllDataPoints(surveyId, geocells));
        } else {
            dataPointList.addAll(listOnlyPublicDataPoints(geocells));
        }

        placemarkList.addAll(marshallDataPointListToDto(dataPointList, isAuthorizedUser));

        response.put("placemarks", placemarkList);
        return response;
    }

    private boolean isAuthorizedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        } else {
            Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
            return auths.contains(AppRole.USER)
                    || auths.contains(AppRole.ADMIN)
                    || auths.contains(AppRole.SUPER_ADMIN);
        }
    }

    private List<SurveyedLocale> listAllDataPoints(Long surveyId, List<String> geocells) {
        return localeDao.listLocalesByGeocell(surveyId, geocells, LIMIT_PLACEMARK_POINTS);
    }

    private List<SurveyedLocale> listOnlyPublicDataPoints(List<String> geocells) {
        return localeDao.listPublicLocalesByGeocell(geocells, LIMIT_PLACEMARK_POINTS);
    }

    private List<PlacemarkDto> marshallDataPointListToDto(List<SurveyedLocale> dataPointList, boolean isAuthorisedUser) {
        if (dataPointList == null) {
            return Collections.emptyList();
        }

        final List<PlacemarkDto> placemarkList = new ArrayList<PlacemarkDto>();
        for (SurveyedLocale dataPoint : dataPointList) {
            if(isAuthorisedUser) {
                placemarkList.add(marshallDataPointToDto(dataPoint));
            } else {
                placemarkList.add(marshallPublicDataPointToDto(dataPoint));
            }
        }
        return placemarkList;
    }

    private PlacemarkDto marshallDataPointToDto(SurveyedLocale dataPoint) {
        final PlacemarkDto dataPointDto = marshallPublicDataPointToDto(dataPoint);
        dataPointDto.setDetailsId(dataPoint.getKey().getId());
        dataPointDto.setSurveyId(dataPoint.getCreationSurveyId());
        // make even to avoid clash with cluster keyIds in client cache
        dataPointDto.setKeyId(dataPoint.getKey().getId() * 2);
        return dataPointDto;
    }

    private PlacemarkDto marshallPublicDataPointToDto(SurveyedLocale dataPoint) {
        final PlacemarkDto dataPointDto = new PlacemarkDto();
        dataPointDto.setLatitude(dataPoint.getLatitude());
        dataPointDto.setLongitude(dataPoint.getLongitude());
        dataPointDto.setCount(1);
        dataPointDto.setLevel(0);
        dataPointDto.setCollectionDate(dataPoint.getLastSurveyedDate());
        return dataPointDto;
    }
}
