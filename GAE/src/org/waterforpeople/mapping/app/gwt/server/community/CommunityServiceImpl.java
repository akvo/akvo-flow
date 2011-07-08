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

	/**
	 * lists all communities for a country
	 */
	@Override
	public CommunityDto[] listCommunities(String countryCode) {
		CommunityDao commDao = new CommunityDao();
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
	@Override
	public CountryDto[] listCountries() {
		CommunityDao commDao = new CommunityDao();
		CountryDto[] dtoList = null;
		CountryDao countryDao = new CountryDao();
		List<Country> cList= countryDao.list("displayName", "asc", "all");
		if (cList != null) {
			dtoList = new CountryDto[cList.size()];
			for (int i = 0; i < cList.size(); i++) {
				CountryDto dto = new CountryDto();
				dto.setIsoAlpha2Code(cList.get(i).getIsoAlpha2Code());
				dto.setName(cList.get(i).getName());
				dtoList[i] = dto;
			}
		}
		return dtoList;
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
}
