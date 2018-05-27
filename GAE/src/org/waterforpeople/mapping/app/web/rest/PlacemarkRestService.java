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

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestParam(value = "bbString", defaultValue = "") String boundingBoxString,
            @RequestParam(value = "gcLevel", defaultValue = "") Integer gcLevel) {

        log.log(Level.FINE, "received request for: " + boundingBoxString + ", " + gcLevel);

        final List<String> geocells = Arrays.asList(boundingBoxString.split(","));
        final Map<String, Object> response = new HashMap<String, Object>();
        final List<PlacemarkDto> placemarkList = new ArrayList<PlacemarkDto>();
        List<SurveyedLocale> slList = new ArrayList<>();

        if(isAuthorizedUser()) {
            slList.addAll(listAllDataPoints(geocells, gcLevel));
        } else {
            slList.addAll(listOnlyPublicDataPoints(geocells, gcLevel));
        }

        if (slList != null) {
            for (SurveyedLocale sl : slList) {
                placemarkList.add(marshallDomainToDto(sl));
            }
        }

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

    private List<SurveyedLocale> listAllDataPoints(List<String> geocells, Integer gcLevel) {
        return localeDao.listLocalesByGeocell(geocells, LIMIT_PLACEMARK_POINTS);
    }

    private List<SurveyedLocale> listOnlyPublicDataPoints(List<String> geocells, Integer gcLevel) {
        return localeDao.listPublicLocalesByGeocell(geocells, LIMIT_PLACEMARK_POINTS);
    }

    private PlacemarkDto marshallDomainToDto(SurveyedLocale sl) {
        final PlacemarkDto dto = new PlacemarkDto();
        dto.setLatitude(sl.getLatitude());
        dto.setLongitude(sl.getLongitude());
        dto.setCount(1);
        dto.setDetailsId(sl.getKey().getId());
        dto.setLevel(0);
        dto.setSurveyId(sl.getCreationSurveyId());
        dto.setCollectionDate(sl.getLastSurveyedDate());
        // make even to avoid clash with cluster keyIds in client cache
        dto.setKeyId(sl.getKey().getId() * 2);
        return dto;
    }
}
