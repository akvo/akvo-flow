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

import com.gallatinsystems.surveyal.dao.SurveyedLocaleClusterDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleCluster;

@Controller
@RequestMapping("/placemarks")
public class PlacemarkRestService {
    final int LIMIT_PLACEMARK_POINTS = 2000;
    private static final Logger log = Logger
            .getLogger(PlacemarkRestService.class.getName());

    private SurveyedLocaleDao localeDao = new SurveyedLocaleDao();

    private SurveyedLocaleClusterDao slcDao = new SurveyedLocaleClusterDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listPlaceMarks(
            @RequestParam(value = "bbString", defaultValue = "") String boundingBoxString,
            @RequestParam(value = "gcLevel", defaultValue = "") Integer gcLevel) {
        // assume we are on the public map
        Boolean allPlacemarks = false;
        log.log(Level.FINE, "received request for: " + boundingBoxString + ", " + gcLevel);

        List<String> geocells = Arrays.asList(boundingBoxString.split(","));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
            if (auths.contains(AppRole.USER) || auths.contains(AppRole.ADMIN)
                    || auths.contains(AppRole.SUPER_ADMIN)) {
                allPlacemarks = true;
            }
        }

        return getPlacemarksReponse(geocells, gcLevel, allPlacemarks);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, Object> placeMarkDetails(@PathVariable("id") Long id) {
        return getPlacemarkResponseById(id);
    }

    private Map<String, Object> getPlacemarksReponse(List<String> geocells, Integer gcLevel,
            Boolean allPlacemarks) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final List<PlacemarkDto> result = new ArrayList<PlacemarkDto>();
        final List<SurveyedLocaleCluster> slcList;
        if (gcLevel > 0) {
            // get clusters on the basis of the geocells list received from the dashboard,
            // and the required level of clustering. The geocells list form the viewport,
            // and in this viewport we still have to determine the right cluster level.
            // The dashboard is responsible for asking for a level that makes sense.
            if (allPlacemarks) {
                slcList = slcDao.listLocaleClustersByGeocell(geocells, gcLevel);
            } else {
                slcList = slcDao.listPublicLocaleClustersByGeocell(geocells, gcLevel);
            }
            if (slcList.size() > 0) {
                for (SurveyedLocaleCluster slc : slcList) {
                    result.add(marshallClusterDomainToDto(slc));
                }
            }
        } else {
            final List<SurveyedLocale> slList = new ArrayList<SurveyedLocale>();
            // get surveyedLocales
            if (allPlacemarks) {
                slList.addAll(localeDao.listLocalesByGeocell(geocells, LIMIT_PLACEMARK_POINTS));
            } else {
                // exclude Household data
                slList.addAll(localeDao
                        .listPublicLocalesByGeocell(geocells, LIMIT_PLACEMARK_POINTS));
            }
            if (slList.size() > 0) {
                for (SurveyedLocale sl : slList) {
                    result.add(marshallDomainToDto(sl));
                }
            }
        }

        response.put("placemarks", result);
        return response;
    }

    private Map<String, Object> getPlacemarkResponseById(Long id) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final SurveyedLocale sl = localeDao.getById(id);

        if (sl == null) {
            throw new HttpMessageNotReadableException("ID not found");
        }

        response.put("placemark", marshallDomainToDto(sl));
        return response;
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

    private PlacemarkDto marshallClusterDomainToDto(SurveyedLocaleCluster slc) {
        final PlacemarkDto dto = new PlacemarkDto();
        dto.setLatitude(slc.getLatCenter());
        dto.setLongitude(slc.getLonCenter());
        dto.setCount(slc.getCount());
        dto.setLevel(slc.getLevel());
        // make odd to avoid clash with cluster keyIds in client cache
        dto.setKeyId(slc.getKey().getId() * 2 + 1);
        if (slc.getCount() == 1) {
            dto.setDetailsId(slc.getFirstSurveyedLocaleId());
            dto.setCollectionDate(slc.getFirstCollectionDate());
        }
        return dto;
    }
}
