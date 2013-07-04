/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
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
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;


@Controller
@RequestMapping("/placemarks")
public class PlacemarkRestService {

	final int LIMIT_PLACEMARK_POINTS = 500;
	private static final Logger log = Logger
			.getLogger(PlacemarkRestService.class.getName());

	@Inject
	SurveyedLocaleDao localeDao;

	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, Object> listPlaceMarks(
			@RequestParam(value = "country", defaultValue = "") String country) {

		if (StringUtils.isEmpty(country)) {
			final String msg = "You must pass a parameter [country]";
			log.log(Level.SEVERE, msg);
			throw new HttpMessageNotReadableException(msg);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null){
			Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
			if (auths.contains(AppRole.USER) || auths.contains(AppRole.ADMIN) || auths.contains(AppRole.SUPER_ADMIN)){
				return getPlacemarksReponseByCountry(country);
			}			
		}
		
		return getPlacemarksReponseByCountryPublic(country);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, Object> placeMarkDetails(@PathVariable("id") Long id) {
		return getPlacemarkResponseById(id);
	}

	private Map<String, Object> getPlacemarksReponseByCountry(String country) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final List<PlacemarkDto> result = new ArrayList<PlacemarkDto>();
		final List<SurveyedLocale> slList = new ArrayList<SurveyedLocale>();

		slList.addAll(localeDao.listBySubLevel(country, null, null, null, null,
				null, LIMIT_PLACEMARK_POINTS));

		if (slList.size() > 0) {
			for (SurveyedLocale ap : slList) {
				result.add(marshallDomainToDto(ap));
			}
		}

		response.put("placemarks", result);
		return response;
	}

	private Map<String, Object> getPlacemarksReponseByCountryPublic(String country) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final List<PlacemarkDto> result = new ArrayList<PlacemarkDto>();
		final List<SurveyedLocale> slList = new ArrayList<SurveyedLocale>();

		// exclude Household data
		slList.addAll(localeDao.listBySubLevel(country, null, null,"Point", null,
				null, LIMIT_PLACEMARK_POINTS));
		slList.addAll(localeDao.listBySubLevel(country, null, null,"PublicInstitution", null,
				null, LIMIT_PLACEMARK_POINTS));

		if (slList.size() > 0) {
			for (SurveyedLocale ap : slList) {
				result.add(marshallDomainToDto(ap));
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
		final String markType = StringUtils.isEmpty(sl.getLocaleType()) ? AccessPointType.WATER_POINT
				.toString() : sl.getLocaleType().toUpperCase();

		dto.setMarkType(markType);
		dto.setLatitude(sl.getLatitude());
		dto.setLongitude(sl.getLongitude());
		dto.setCollectionDate(sl.getLastUpdateDateTime());
		dto.setKeyId(sl.getKey().getId());
		return dto;
	}
}
