/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.server.community;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.community.SubCountryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.domain.Community;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.dao.SubCountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.geography.domain.SubCountry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Lists country and community information
 * 
 * @author Christopher Fagiani
 * 
 */
public class CommunityServiceImpl extends RemoteServiceServlet implements
		CommunityService {

	private static final long serialVersionUID = -1711427640212917332L;
	private CountryDao countryDao;
	private CommunityDao commDao;

	public CommunityServiceImpl() {
		countryDao = new CountryDao();
		commDao = new CommunityDao();
	}

	/**
	 * lists all communities for a country
	 */
	@Override
	public CommunityDto[] listCommunities(String countryCode) {

		CommunityDto[] dtoList = null;
		List<Community> commList = commDao.listCommunityByCountry(countryCode);
		if (commList != null) {
			dtoList = new CommunityDto[commList.size()];
			for (int i = 0; i < commList.size(); i++) {
				CommunityDto dto = new CommunityDto();
				dto.setCommunityCode(commList.get(i).getCommunityCode());
				dtoList[i] = dto;
			}
		}
		return dtoList;
	}

	/**
	 * lists all countries
	 */
	@Deprecated
	@Override
	public CountryDto[] listCountries() {

		CountryDto[] dtoList = null;

		List<Country> cList = countryDao.list("displayName", "asc", "all");
		if (cList != null) {
			dtoList = new CountryDto[cList.size()];
			for (int i = 0; i < cList.size(); i++) {
				dtoList[i] = convertToCountryDto(cList.get(i));
			}
		}
		return dtoList;
	}

	private CountryDto convertToCountryDto(Country c) {
		CountryDto dto = new CountryDto();
		dto.setIsoAlpha2Code(c.getIsoAlpha2Code());
		dto.setName(c.getName());
		if (c.getDisplayName() == null) {
			dto.setDisplayName(c.getName());
		} else {
			dto.setDisplayName(c.getDisplayName());
		}
		dto.setCentroidLat(c.getCentroidLat());
		dto.setCentroidLon(c.getCentroidLon());
		dto.setIncludeInExternal(c.getIncludeInExternal());
		dto.setIncludeInKMZ(c.getIncludeInKMZ());
		dto.setKeyId(c.getKey().getId());
		return dto;

	}

	/**
	 * returns a paginated list of countries
	 * 
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<CountryDto>> listCountries(String cursor) {
		List<Country> countries = countryDao
				.list("displayName", "desc", cursor);
		ResponseDto<ArrayList<CountryDto>> response = new ResponseDto<ArrayList<CountryDto>>();
		if (countries != null) {
			ArrayList<CountryDto> dtoList = new ArrayList<CountryDto>();
			for (Country c : countries) {
				dtoList.add(convertToCountryDto(c));
			}
			response.setPayload(dtoList);
			response.setCursorString(CountryDao.getCursor(countries));
		}

		return response;
	}

	/**
	 * lists sub countries with the parent passed in. If no parent is passed in,
	 * all level 1 subcountry records for that country will be returned.
	 * 
	 * @param country
	 * @param parentId
	 * @return
	 */
	@Override
	public List<SubCountryDto> listChildSubCountries(String country,
			Long parentId) {

		SubCountryDao subDao = new SubCountryDao();
		List<SubCountryDto> results = null;
		List<SubCountry> subCountries = null;
		if (parentId != null) {
			subCountries = subDao.listSubCountryByParent(parentId);
		} else {
			subCountries = subDao.listSubCountryByLevel(country, 1, null);
		}
		if (subCountries != null) {
			results = new ArrayList<SubCountryDto>();
			for (SubCountry c : subCountries) {
				SubCountryDto dto = new SubCountryDto();
				DtoMarshaller.copyToDto(c, dto);
				results.add(dto);
			}
		}

		return results;
	}

	@Override
	public CountryDto saveCountry(CountryDto countryDto) {
		Country country = new Country();
		DtoMarshaller.copyToCanonical(country, countryDto);
		country = countryDao.save(country);
		countryDto.setKeyId(country.getKey().getId());
		return countryDto;
	}
}
