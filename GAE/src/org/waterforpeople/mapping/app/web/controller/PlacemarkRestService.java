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

package org.waterforpeople.mapping.app.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

@Controller
@RequestMapping("/placemark")
public class PlacemarkRestService {

	@Inject
	SurveyedLocaleDao localeDao;

	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public List<PlacemarkDto> listPlaceMarks(
			@RequestParam("country") String country) {
		List<PlacemarkDto> result = new ArrayList<PlacemarkDto>();

		List<SurveyedLocale> slList = localeDao.listBySubLevel(country, null,
				null, null, null, null, null);

		for (SurveyedLocale ap : slList) {
			result.add(marshallDomainToDto(ap));
		}
		return result;
	}

	private PlacemarkDto marshallDomainToDto(SurveyedLocale sl) {
		final PlacemarkDto dto = new PlacemarkDto();
		final String markType = StringUtils.isEmpty(sl.getLocaleType()) ? AccessPointType.WATER_POINT
				.toString() : sl.getLocaleType().toUpperCase();

		dto.setMarkType(markType);
		dto.setLatitude(sl.getLatitude());
		dto.setLongitude(sl.getLongitude());
		dto.setKeyId(sl.getKey().getId());
		dto.setCollectionDate(sl.getLastUpdateDateTime());

		return dto;
	}

	public class PlacemarkDto extends BaseDto {
		private static final long serialVersionUID = 2520698898060952743L;
		private Double latitude = null;
		private Double longitude = null;
		private Long altitude = null;
		private String markType = null;
		private Date collectionDate = null;

		public Double getLatitude() {
			return latitude;
		}

		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public Double getLongitude() {
			return longitude;
		}

		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		public Long getAltitude() {
			return altitude;
		}

		public void setAltitude(Long altitude) {
			this.altitude = altitude;
		}

		public String getMarkType() {
			return markType;
		}

		public void setMarkType(String markType) {
			this.markType = markType;
		}

		public Date getCollectionDate() {
			return collectionDate;
		}

		public void setCollectionDate(Date collectionDate) {
			this.collectionDate = collectionDate;
		}
	}
}
