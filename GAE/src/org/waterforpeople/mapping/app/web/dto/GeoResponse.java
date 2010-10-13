package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;

import com.gallatinsystems.framework.rest.RestResponse;

/**
 * responses from GeoServlet
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoResponse extends RestResponse {

	private static final long serialVersionUID = -4053566733640591308L;
	private List<CountryDto> countries;
	private List<CommunityDto> communities;

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}

	public List<CommunityDto> getCommunities() {
		return communities;
	}

	public void setCommunities(List<CommunityDto> communities) {
		this.communities = communities;
	}

}
